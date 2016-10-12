package com.zgd.common;

import java.util.Comparator;
import java.util.Map;

public class PiaComparator implements Comparator<Map<String, Object>> {

	private String comparatorEl;

	public PiaComparator(String value) {
		this.comparatorEl = value;
	}

	@Override
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		int no1 = CommonUtil.ObejctToInt(o1.get(comparatorEl));
		int no2 = CommonUtil.ObejctToInt(o2.get(comparatorEl));
		if (no1 > no2) {
			return -1;
		} else if (no1 == no2) {
			return 0;
		} else {
			return 1;
		}
	}

}
