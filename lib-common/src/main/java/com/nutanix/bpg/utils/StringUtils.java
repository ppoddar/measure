package com.nutanix.bpg.utils;

import java.util.Arrays;
import java.util.Iterator;

public class StringUtils {
	
	/**
	 * join given string separated by comma.
	 * @param names items to join. 
	 * @return a concatenated string
	 */
	public static String join(char sep, Iterator<?> elements) {
		String s = "";
		while (elements.hasNext()) {
			Object e = elements.next();
			s += (e == null ? "null" : e.toString());
			if (elements.hasNext()) s += sep;
		}
		return s;
	}
	
	public static String join(char sep, Iterable<?> elements) {
		return join(sep, elements.iterator());
	}
	
	public static String join(char sep, Object[] elements) {
		return join(sep, Arrays.asList(elements));
	}
	
	
	public static String repeat(int n, String s) {
		String joined = "";
		for (int i = 0; i < n - 1; i++) {
			joined += s + ",";
		}
		joined += s;
		return joined;
	}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

}
