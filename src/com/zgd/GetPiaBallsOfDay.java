package com.zgd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.zgd.common.PiaComparator;

@SuppressWarnings("serial")
public class GetPiaBallsOfDay extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String playDate = CommonUtil.ObejctToString(req.getParameter("playDate"));
		String sortKind = CommonUtil.ObejctToString(req.getParameter("sortKind"));
		
		String strUrl = req.getRequestURL().toString();
		String startTaino = "683";
		String endTaino = "696";
		if(strUrl.indexOf("piadatatest") > 0){
			startTaino = "683";
			endTaino = "689";
		}
		List<Map<String, Object>> list = getBallOutUntilSomeDay(playDate, sortKind,startTaino,endTaino);

		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}

	public static List<Map<String, Object>> getBallOutUntilSomeDay(String playDate, String sortKind, String startTaino, String endTaino) {

		// 検索条件
		List<String> etiqueta = new ArrayList<String>();
		for (int i = CommonUtil.ObejctToInt(startTaino); i <= CommonUtil.ObejctToInt(endTaino); i++) {
			etiqueta.add(CommonUtil.ObejctToString(i));
		}

		List<Filter> list = new ArrayList<Filter>();
		if (!CommonUtil.IsNullOrEmpty(playDate)) {
			list.add(new FilterPredicate("playDate", FilterOperator.LESS_THAN_OR_EQUAL, playDate));
		}
		list.add(new FilterPredicate("taiNo", FilterOperator.IN, etiqueta));
		// 検索条件設定
		Query q = new Query("PIA_DATA_INFO").addSort("taiNo", SortDirection.ASCENDING).addSort("playDate",
				SortDirection.DESCENDING);
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

		int totalOut = 0;

		int totalOutBefore = 0;
		String tainoKey = startTaino;

		Map<String, Object> firstEl = new HashMap<String, Object>();
		Map<String, Object> secondEl = new HashMap<String, Object>();
		Map<String, Object> thirdEl = new HashMap<String, Object>();
		Map<String, Object> furthEl = new HashMap<String, Object>();
		Map<String, Object> fifthEl = new HashMap<String, Object>();
		int sortIndex = 0;
		// データを洗い出す
		for (Entity en : pq.asIterable()) {
			String tai = CommonUtil.ObejctToString(en.getProperty("taiNo"));

			if (!tai.equals(tainoKey)) {
				map.put("totalOut", totalOut);
				map.put("totalOutBefore", totalOutBefore);
				map.putAll(firstEl);
				map.putAll(CommonUtil.convertKeyOfMap(secondEl, "1"));
				map.putAll(CommonUtil.convertKeyOfMap(thirdEl, "2"));
				map.putAll(CommonUtil.convertKeyOfMap(furthEl, "3"));
				map.putAll(CommonUtil.convertKeyOfMap(fifthEl, "4"));
				listMap.add(map);
				map = new HashMap<String, Object>();
				firstEl = new HashMap<String, Object>();
				secondEl = new HashMap<String, Object>();
				thirdEl = new HashMap<String, Object>();
				fifthEl = new HashMap<String, Object>();
				tainoKey = tai;
				totalOut = 0;
				totalOutBefore = 0;
				sortIndex = 0;
			}
			int balls = CommonUtil.ObejctToInt(en.getProperty("ballOutput"));
			int ballsBefore = balls;
			if (sortIndex == 0) {
				firstEl = en.getProperties();
				ballsBefore = 0;
			}
			if (sortIndex == 1) {
				secondEl = en.getProperties();
			}
			if (sortIndex == 2) {
				thirdEl = en.getProperties();
			}
			if (sortIndex == 3) {
				furthEl = en.getProperties();
			}
			if (sortIndex == 4) {
				fifthEl = en.getProperties();
			}
			totalOut += balls;
			totalOutBefore += ballsBefore;
			sortIndex++;
		}
		if (tainoKey.equals(endTaino)) {
			map = new HashMap<String, Object>();
			map.put("totalOutBefore", totalOutBefore);
			map.put("totalOut", totalOut);
			map.putAll(firstEl);
			map.putAll(CommonUtil.convertKeyOfMap(secondEl, "1"));
			map.putAll(CommonUtil.convertKeyOfMap(thirdEl, "2"));
			map.putAll(CommonUtil.convertKeyOfMap(furthEl, "3"));
			map.putAll(CommonUtil.convertKeyOfMap(fifthEl, "4"));
			listMap.add(map);
		}
		
		Collections.sort(listMap, new PiaComparator(sortKind));
		int index = 1;
		List<Map<String, Object>> listMap1 = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m : listMap) {
			map = new HashMap<String, Object>();
			map.putAll(m);
			map.put("rank", CommonUtil.ObejctToString(index));
			index++;
			listMap1.add(map);
		}
		return listMap1;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
