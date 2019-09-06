package com.nutanix.bpg.measure.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.nutanix.bpg.measure.model.Named;

public class NamedMap<V extends Named> {
	private final Map<String, V> map;
	
	public NamedMap() {
		map = new LinkedHashMap<String, V>();
	}
	
	public void add(V v) {
		if (v == null) throw new IllegalArgumentException("null element can not be added");
		if (v.getName() == null) throw new IllegalArgumentException("element with null name can not be added");
		if (v.getName().trim().isEmpty()) throw new IllegalArgumentException("element with empty name can not be added");
		map.put(v.getName(), v);
	}
	
	public boolean containsKey(String name) {
		return map.containsKey(name);
	}
	
	public V get(String name) {
		return get(name, true);
	}
	
	public V get(String name, boolean mustExist) {
		if (!map.containsKey(name)) {
			if (mustExist) {
				throw new IllegalArgumentException("no element named [" + name + "]"
						+ " Available elements are " + getNames());
			} else {
				return null;
			}
		}
		return (V)map.get(name);
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
