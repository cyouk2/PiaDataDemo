package com.zgd.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonUtil {

	public static boolean IsNullOrEmpty(Object obj) {

		if (obj == null)
			return true;
		if ("".equals(String.valueOf(obj).trim()))
			return true;
		return false;
	}

	public static String ObejctToString(Object obj) {

		if (obj == null)
			return "";
		if ("".equals(String.valueOf(obj).trim()))
			return "";
		return String.valueOf(obj).trim();
	}

	public static int ObejctToInt(Object obj) {
		int a = 0;
		if (obj == null)
			return a;
		try {
			a = Integer.parseInt(String.valueOf(obj).trim());
		} catch (Exception e) {
			return 0;
		}
		return a;
	}

	public static List<Map<String, Object>> MergeMap(List<Map<String, Object>> map1, List<Map<String, Object>> map2,
			String key, String valueKey, String index) {
		List<Map<String, Object>> alist = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> e : map1) {
			String key1 = CommonUtil.ObejctToString(e.get(key));

			for (Map<String, Object> e1 : map1) {
				String key2 = CommonUtil.ObejctToString(e.get(key));
				if (key1.equals(key2)) {
					e.put(valueKey + index, e1.get(valueKey));
					alist.add(e);
				}
			}
		}
		return alist;
	}

}
