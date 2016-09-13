package com.zgd.common;

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
}
