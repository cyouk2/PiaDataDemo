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
		String playDate = CommonUtil.ObejctToString(req.getParameter("playDate"));
		List<Map<String, Object>> list = getBallOutUntilSomeDay(playDate);
		Map<String, Object> a = list.get(0);
		Map<String, Object> ab = list.get(1);
		list.remove(0);
		list.remove(0);
		List<Map<String, Object>> list1 = GetPiaDataByDate.getTaiInfoByDate(playDate);
		List<Map<String, Object>> list2 = GetPiaDataByDate.getTaiInfoByDate(CommonUtil.addDay(playDate, 1));
		List<Map<String, Object>> list3 = GetPiaDataByDate.getTaiInfoByDate(CommonUtil.addDay(playDate, 2));

		List<Map<String, Object>> list4 = CommonUtil.MergeMap(list, list1, "taiNo", "1", "rate", "ballOutput");
		List<Map<String, Object>> list5 = CommonUtil.MergeMap(list4, list2, "taiNo", "2", "rate", "ballOutput");
		List<Map<String, Object>> list6 = CommonUtil.MergeMap(list5, list3, "taiNo", "3", "rate", "ballOutput");
		
		list6.add(0, a);
		list6.add(1, ab);
		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list6);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}

	public static List<Map<String, Object>> getBallOutUntilSomeDay(String playDate) {

		// 検索条件
		List<String> etiqueta = new ArrayList<String>();
		for (int i = 557; i <= 584; i++) {
			etiqueta.add(CommonUtil.ObejctToString(i));
		}

		List<Filter> list = new ArrayList<Filter>();
		if (!CommonUtil.IsNullOrEmpty(playDate)) {
			list.add(new FilterPredicate("playDate", FilterOperator.LESS_THAN_OR_EQUAL, playDate));
		}
		list.add(new FilterPredicate("taiNo", FilterOperator.IN, etiqueta));
		// 検索条件設定
		Query q = new Query("PIA_DATA").addSort("taiNo", SortDirection.ASCENDING);
		if (list.size() == 1) {
			q.setFilter(list.get(0));
		} else {
			q.setFilter(new CompositeFilter(CompositeFilterOperator.AND, list));
		}
		// 検索処理実行
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();

		int outTotal = 0;
		int outAll = 0;
		String tainoKey = "557";
		// データを洗い出す
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
			int balls = CommonUtil.ObejctToInt(en.getProperty("ballOutput"));
			outTotal += balls;
			outAll += balls;

		}
		if (tainoKey.equals("584")) {
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
					return -1;
				} else if (no1 == no2) {
					return 0;
				} else {
					return 1;
				}
			}
		};
		Collections.sort(listMap, mapComparator);
		int index = 1;
		List<Map<String, Object>> listMap1 = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> m : listMap){
			map = new HashMap<String, Object>();
			map.putAll(m);
			map.put("rank",CommonUtil.ObejctToString(index));
			index ++;
			listMap1.add(map);
		}
		
		
		map = new HashMap<String, Object>();
		map.put("taiNo", "TOTAL");
		map.put("outTotal", outAll);
		listMap1.add(0, map);
		map = new HashMap<String, Object>();
		map.put("taiNo", "AVERAGE");
		map.put("outTotal", (int) (outAll / 28));
		listMap1.add(1, map);
		return listMap1;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
