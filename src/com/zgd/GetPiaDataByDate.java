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
		resp.getWriter().println( gson.toJson(re));
	}
	
	public static List<Map<String, Object>> getTallestPeople(String playDate) {

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		List<Filter> list = new ArrayList<Filter>();
		
		list.add(new FilterPredicate("playDate", FilterOperator.EQUAL, playDate));
		list.add(new FilterPredicate("taiNo", FilterOperator.GREATER_THAN_OR_EQUAL, "557"));
		list.add(new FilterPredicate("taiNo", FilterOperator.LESS_THAN_OR_EQUAL, "584"));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, list);
		Query q = new Query("PIA_DATA").setFilter(filter).addSort("taiNo", SortDirection.ASCENDING);
		PreparedQuery pq = datastore.prepare(q);
		
		for (Entity en : pq.asIterable()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll(ConvertListOfEntity(en.getProperties()));
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
		String playDate =CommonUtil.ObejctToString(map.get("playDate"));
		if (rate == 0){
			listMap.put("rateN", 0);
		
		}else {
			listMap.put("rateN", (int)(10000 / rate));
		}

		listMap.put("bonusCountN", bonusCount * 10);
		listMap.put("ballInputN", ballInput / 10);
		listMap.put("ballOutputN", ballOutput / 100);
		listMap.put("playDateN", playDate.substring(4));

		return listMap;
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
