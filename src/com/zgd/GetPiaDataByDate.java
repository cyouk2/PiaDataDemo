package com.zgd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.zgd.common.ComRootResult;
import com.zgd.common.CommonUtil;

@SuppressWarnings("serial")
public class GetPiaDataByDate extends HttpServlet {
	// private static final Logger log =
	// Logger.getLogger(GetPiaDataByDate.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String playDate = req.getParameter("playDate");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (!CommonUtil.IsNullOrEmpty(playDate)) {
			list = getTallestPeople(playDate);
		}
		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}

	public static List<Map<String, Object>> getTallestPeople(String playDate) {

		List<Filter> list = new ArrayList<Filter>();
		list.add(new FilterPredicate("playDate", FilterOperator.EQUAL, playDate));
		list.add(new FilterPredicate("taiNo", FilterOperator.GREATER_THAN_OR_EQUAL, "557"));
		list.add(new FilterPredicate("taiNo", FilterOperator.LESS_THAN_OR_EQUAL, "584"));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, list);
		Query q = new Query("PIA_DATA").setFilter(filter).addSort("taiNo", SortDirection.ASCENDING);

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Map<String, Object>> listOfGetPiaBallsOfDay = getPiaBallsOfDay(playDate);

		for (Entity en : pq.asIterable()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll(ConvertListOfEntity(en.getProperties()));
			String taiNo1 = CommonUtil.ObejctToString(en.getProperty("taiNo"));
			for (Map<String, Object> e : listOfGetPiaBallsOfDay) {
				String taie = CommonUtil.ObejctToString(e.get("taiNo"));
				if (taiNo1.equals(taie)) {
					map.put("totalOut", (int) (CommonUtil.ObejctToInt(e.get("totalOut")) / 100));
				}
			}
			listMap.add(map);
		}
		return listMap;
	}

	public static Map<String, Object> ConvertListOfEntity(Map<String, Object> map) {

		Map<String, Object> listMap = new HashMap<String, Object>();
		listMap.putAll(map);

		int bonusCount = CommonUtil.ObejctToInt(map.get("bonusCount"));
		int ballInput = CommonUtil.ObejctToInt(map.get("ballInput"));
		int ballOutput = CommonUtil.ObejctToInt(map.get("ballOutput"));
		int rate = CommonUtil.ObejctToInt(map.get("rate"));
		String playDate = CommonUtil.ObejctToString(map.get("playDate"));

		listMap.put("bonusCountN", bonusCount * 10);
		listMap.put("ballInputN", ballInput / 10);
		listMap.put("ballOutputN", ballOutput / 100);
		listMap.put("playDateN", playDate.substring(4));

		if (rate == 0) {
			listMap.put("rateN", 0);

		} else {
			listMap.put("rateN", (int) (10000 / rate));
		}

		return listMap;
	}

	public static List<Map<String, Object>> getPiaBallsOfDay(String playDate) {
		String tainoKey = "557";

		List<Filter> list = new ArrayList<Filter>();
		List<String> etiqueta = new ArrayList<String>();
		for (int i = 557; i <= 584; i++) {
			etiqueta.add(CommonUtil.ObejctToString(i));
		}
		list.add(new FilterPredicate("playDate", FilterOperator.LESS_THAN_OR_EQUAL, playDate));
		list.add(new FilterPredicate("taiNo", FilterOperator.IN, etiqueta));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, list);
		Query q = new Query("PIA_DATA").addSort("taiNo", SortDirection.ASCENDING).setFilter(filter);

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		Map<String, Object> map = new HashMap<String, Object>();
		int outTotal = 0;
		for (Entity en : pq.asIterable()) {

			String tai = CommonUtil.ObejctToString(en.getProperty("taiNo"));
			if (!tai.equals(tainoKey)) {
				map.put("taiNo", tainoKey);
				map.put("totalOut", outTotal);
				listMap.add(map);
				map = new HashMap<String, Object>();
				tainoKey = tai;
				outTotal = 0;
			}
			outTotal += CommonUtil.ObejctToInt(en.getProperty("ballOutput"));
		}
		return listMap;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
