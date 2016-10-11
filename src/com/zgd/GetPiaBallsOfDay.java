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
		String sortKind = CommonUtil.ObejctToString(req.getParameter("sortKind"));

		List<Map<String, Object>> list = getBallOutUntilSomeDay(playDate, sortKind);

		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}

	public static List<Map<String, Object>> getBallOutUntilSomeDay(String playDate, String sortKind) {

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
		Query q = new Query("PIA_DATA").addSort("taiNo", SortDirection.ASCENDING).addSort("playDate",
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
		String tainoKey = "557";

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
		if (tainoKey.equals("584")) {
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

		Comparator<Map<String, Object>> mapComparator = new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				int no1 = CommonUtil.ObejctToInt(m1.get("totalOut"));
				int no2 = CommonUtil.ObejctToInt(m2.get("totalOut"));
				if (no1 > no2) {
					return -1;
				} else if (no1 == no2) {
					return 0;
				} else {
					return 1;
				}
			}
		};

		Comparator<Map<String, Object>> mapComparatorBefore = new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				int no1 = CommonUtil.ObejctToInt(m1.get("totalOutBefore"));
				int no2 = CommonUtil.ObejctToInt(m2.get("totalOutBefore"));
				if (no1 > no2) {
					return -1;
				} else if (no1 == no2) {
					return 0;
				} else {
					return 1;
				}
			}
		};
		Comparator<Map<String, Object>> mapComparatorrate = new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				int no1 = CommonUtil.ObejctToInt(m1.get("rate"));
				int no2 = CommonUtil.ObejctToInt(m2.get("rate"));
				if (no1 > no2) {
					return -1;
				} else if (no1 == no2) {
					return 0;
				} else {
					return 1;
				}
			}
		};
		Comparator<Map<String, Object>> mapComparatorrate1 = new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				int no1 = CommonUtil.ObejctToInt(m1.get("rate1"));
				int no2 = CommonUtil.ObejctToInt(m2.get("rate1"));
				if (no1 > no2) {
					return -1;
				} else if (no1 == no2) {
					return 0;
				} else {
					return 1;
				}
			}
		};
		Comparator<Map<String, Object>> mapComparatorballOutput = new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				int no1 = CommonUtil.ObejctToInt(m1.get("ballOutput"));
				int no2 = CommonUtil.ObejctToInt(m2.get("ballOutput"));
				if (no1 > no2) {
					return -1;
				} else if (no1 == no2) {
					return 0;
				} else {
					return 1;
				}
			}
		};
		if (sortKind.equals("totalOut")) {
			Collections.sort(listMap, mapComparator);
		} else if (sortKind.equals("totalOutBefore")) {
			Collections.sort(listMap, mapComparatorBefore);
		} else if (sortKind.equals("rate")) {
			Collections.sort(listMap, mapComparatorrate);
		} else if (sortKind.equals("rate1")) {
			Collections.sort(listMap, mapComparatorrate1);
		} else if (sortKind.equals("ballOutput")) {
			Collections.sort(listMap, mapComparatorballOutput);
		}
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
