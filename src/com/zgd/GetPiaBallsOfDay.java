package com.zgd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class GetPiaBallsOfDay extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String playDate = req.getParameter("playDate");
		List<Map<String, Object>> list = getTallestPeople(playDate);
		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}

	public static List<Map<String, Object>> getTallestPeople(String playDate) {

		List<String> etiqueta = new ArrayList<String>();
		for (int i = 557; i <= 584; i++) {
			etiqueta.add(CommonUtil.ObejctToString(i));
		}
		
		List<Filter> list = new ArrayList<Filter>();
		list.add(new FilterPredicate("playDate", FilterOperator.LESS_THAN_OR_EQUAL, playDate));
		list.add(new FilterPredicate("taiNo", FilterOperator.IN, etiqueta));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, list);
		
		Query q = new Query("PIA_DATA").addSort("taiNo", SortDirection.ASCENDING).setFilter(filter);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		int outTotal = 0;
		String tainoKey = "557";
		for (Entity en : pq.asIterable()) {
			String tai = CommonUtil.ObejctToString(en.getProperty("taiNo"));
			if (!tai.equals(tainoKey)) {
				map.put("taiNo", tainoKey);
				map.put("outTotal", outTotal);
				listMap.add(map);
				map = new HashMap<String, Object>();
				tainoKey = tai;
				outTotal = 0;
			}
			outTotal += CommonUtil.ObejctToInt(en.getProperty("ballOutput"));
		}
		if (tainoKey.equals("584")){
			map = new HashMap<String, Object>();
			map.put("taiNo", "584");
			map.put("outTotal", outTotal);
			listMap.add(map);
		}
		Comparator<Map<String, Object>> mapComparator = new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				int no1 = CommonUtil.ObejctToInt(m1.get("outTotal"));
				int no2 = CommonUtil.ObejctToInt(m2.get("outTotal"));
				if (no1 > no2) {
					return 1;
				} else if (no1 == no2) {
					return 0;
				} else {
					return -1;
				}
			}
		};
		Collections.sort(listMap, mapComparator);
		return listMap;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
