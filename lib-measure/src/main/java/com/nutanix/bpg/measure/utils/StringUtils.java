package com.nutanix.bpg.measure.utils;

import java.util.Arrays;
import java.util.Iterator;

public class StringUtils {
	private static final String COMMA = ",";
	
	/**
	 * join given string separated by comma.
	 * @param names items to join. 
	 * @return a concatenated string
	 */
	public static String join(String...names) {
		return join(COMMA, Arrays.asList(names).iterator());
	}
	
	public static String join(Iterable<String> names) {
		return join(COMMA,  names.iterator());
	}
	
	
	public static String joinString(char sep, Iterator<String> elements) {
		String s = "";
		while (elements.hasNext()) {
			s += elements.next();
			if (elements.hasNext()) s += sep;
		}
		return s;
	}
	
	public static String join(String sep, Iterable<String> names) {
		return join(sep, names.iterator());
	}
	public static String join(String sep, String[] names) {
		return join(sep, Arrays.asList(names).iterator());
	}
	
	
	public static String join(String sep, Iterator<String> iterator) {
		String result = "";
		while (iterator.hasNext()) {
			String item = iterator.next();
			if (item == null) { 
				item = "";
			}
			result += item + (iterator.hasNext() ? sep : "");
		}
		return result;
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
