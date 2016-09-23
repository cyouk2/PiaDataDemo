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
			String key, String index, String...valueKey) {
		List<Map<String, Object>> alist = new ArrayList<Map<String, Object>>();
		if(map2 != null && map2.size() != 0){
			for (Map<String, Object> e : map1) {
				String key1 = CommonUtil.ObejctToString(e.get(key));

				for (Map<String, Object> e1 : map2) {
					String key2 = CommonUtil.ObejctToString(e1.get(key));
					if (key1.equals(key2)) {
						for(String i : valueKey){
							e.put(i + index, e1.get(i));
						}
						
						alist.add(e);
					}
				}
			}
		}else{
			return map1;
		}
		
		return alist;
	}

}
