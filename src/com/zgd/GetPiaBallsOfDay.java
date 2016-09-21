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
public class GetPiaBallsOfDay extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<Map<String, Object>> list = getTallestPeople();
		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}

	public static List<Map<String, Object>> getTallestPeople() {

		List<Filter> list = new ArrayList<Filter>();
		list.add(new FilterPredicate("taiNo", FilterOperator.GREATER_THAN_OR_EQUAL, "557"));
		list.add(new FilterPredicate("taiNo", FilterOperator.LESS_THAN_OR_EQUAL, "584"));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, list);
		Query q = new Query("PIA_DATA").addSort("taiNo", SortDirection.ASCENDING).setFilter(filter);

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		String tainoKey = "557";
		Map<String, Object> map = new HashMap<String, Object>();
		int outTotal = 0;
		for (Entity en : pq.asIterable()) {
			
			String tai = CommonUtil.ObejctToString(en.getProperty("taiNo"));
			if (!tai.equals(tainoKey)){
				map.put("taiNo", tainoKey);
				map.put("outTotal", outTotal);
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
