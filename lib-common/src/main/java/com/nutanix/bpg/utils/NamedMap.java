package com.nutanix.bpg.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NamedMap<V extends Named> {
	private final Map<String, V> map;
	private final boolean caseinsenstive;
	
	public NamedMap() {
		this(false);
	}
	
	public NamedMap(boolean flag) {
		map = new LinkedHashMap<String, V>();
		caseinsenstive = flag;
	}
	
	String createKey(String s) {
		return caseinsenstive ? s.toLowerCase() : s;
	}
	
	public void add(V v) {
		if (v == null) throw new IllegalArgumentException("null element can not be added");
		if (v.getName() == null) throw new IllegalArgumentException("element with null name can not be added");
		if (v.getName().trim().isEmpty()) throw new IllegalArgumentException("element with empty name can not be added");
		
		String key = createKey(v.getName());
		map.put(key, v);
	}
	
	public boolean containsKey(String name) {
		String key = createKey(name);
		return map.containsKey(key);
	}
	
	public V get(String name) {
		String key = createKey(name);
		return get(key, true);
	}
	
	public V get(String name, boolean mustExist) {
		String key = createKey(name);
		if (!map.containsKey(key)) {
			if (mustExist) {
				throw new IllegalArgumentException("no element named [" + name + "]"
						+ " Available elements are " + getNames());
			} else {
				return null;
			}
		}
		return (V)map.get(key);
	}
	
	
	public Set<String> getNames() {
		return map.keySet();
	}
	
	public Collection<V> values() {
		return map.values();
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public int size() {
		return map.size();
	}
	

}
