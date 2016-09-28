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
		List<Map<String, Object>> list = getTaiInfoByDate(playDate);
		List<Map<String, Object>> list1 = getTaiInfoByDate(CommonUtil.addDay(playDate, -1));
		List<Map<String, Object>> list6 = GetPiaBallsOfDay.getBallOutUntilSomeDay(CommonUtil.addDay(playDate, -1));
		list = CommonUtil.MergeMap(list, list6, "taiNo", "", "outTotal");
		list = CommonUtil.MergeMap(list, list1, "taiNo", "1", "rate","ballOutput");
		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}

	public static List<Map<String, Object>> getTaiInfoByDate(String playDate) {

		// 検索条件
		List<String> etiqueta = new ArrayList<String>();
		for (int i = 557; i <= 584; i++) {
			etiqueta.add(CommonUtil.ObejctToString(i));
		}
		List<Filter> list = new ArrayList<Filter>();
		list.add(new FilterPredicate("playDate", FilterOperator.EQUAL, playDate));
		list.add(new FilterPredicate("taiNo", FilterOperator.IN, etiqueta));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, list);
		Query q = new Query("PIA_DATA").setFilter(filter).addSort("ballOutput", SortDirection.DESCENDING);

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		for (Entity en : pq.asIterable()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll(en.getProperties());
			listMap.add(map);
		}
		return listMap;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
