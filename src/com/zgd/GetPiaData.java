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
public class GetPiaData extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String playDate = req.getParameter("playDate");
		String taiNo = req.getParameter("taiNo");

		List<Map<String, Object>> list = getTallestPeople(playDate, taiNo);

		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println( gson.toJson(re));
	}

	public static List<Map<String, Object>> getTallestPeople(String playDate, String taiNo) {
		
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<Filter> list = new ArrayList<Filter>();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		if (!CommonUtil.IsNullOrEmpty(playDate)) {
			list.add(new FilterPredicate("playDate", FilterOperator.EQUAL, playDate));
		}
		if (!CommonUtil.IsNullOrEmpty(taiNo)) {
			list.add(new FilterPredicate("taiNo", FilterOperator.EQUAL, taiNo));
		}
	
		Query q = new Query("PIA_DATA_INFO").addSort("playDate", SortDirection.ASCENDING);
		
		if (list.size() > 1) {
			CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, list);
			q.setFilter(filter);
		} else if (list.size() == 1) {
			q.setFilter(list.get(0));
		}
		
		PreparedQuery pq = datastore.prepare(q);
		for (Entity en : pq.asIterable()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll(en.getProperties());
			map.put("id", String.valueOf(en.getKey().getId()));
			map.put("kind", en.getKey().getKind());
			listMap.add(map);
		}
		return listMap;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
