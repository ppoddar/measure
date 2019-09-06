package com.nutanix.bpg.measure.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Named;

/**
 * A data structure to maintain {@link Named named items} 
 * that can be looked up {@link #getByName(String) by name}
 * or {@link #getByIndex(int) by position}.
 * 
 * @author pinaki.poddar
 *
 * @param <V> type of {@link Named named item} to be stored.
 */
public class IndexibleMap<V extends Named> implements Iterable<V> {
	private Map<String, V> values = new LinkedHashMap<String, V>();
	private Map<Integer, String> positions = new HashMap<Integer, String>();
	private static final Logger logger = LoggerFactory.getLogger(IndexibleMap.class);
	/**
	 * adds an item to this receiver.
	 * @param v value to put
	 * @return position where given item is saved,
	 * which is equal to size before the item is added.
	 */
	public int put(V v) {
		if (v == null) {
			throw new IllegalArgumentException("null can not be put into indexible map");
		}
		if (v.getName() == null) {
			throw new IllegalArgumentException("element of " + v.getClass() + " with null name can not be put into indexible map");
		}
		int pos = values.size();
		
		logger.trace("put value at " + pos + " -> " + pos);
		put(v, pos);
		
		return pos;
	}
	
	/**
	 * adds an item to this receiver at given position.
	 * @param v an item
	 * @param pos position when given item be added
	 * @return an item that existed before at given position,
	 * on null.
	 */
	public V put(V v, int pos) {
		V existing = null;
		if (positions.containsKey(pos)) {
			existing = getByIndex(pos);
		}
		if (v.getName() == null) {
			throw new IllegalArgumentException("can not put element " + v.getClass()
			+ " with null name");
		}
		values.put(v.getName(), v);
		positions.put(pos, v.getName());
		if (existing != null) {
			positions.put(values.size(), existing.getName());
			values.put(existing.getName(), existing);
		} 
		return existing;
	}

	
	/**
	 * gets an item by its position
	 * @param i 0-based  position
	 * @return value at given position
	 */
	public V getByIndex(int i) {
		if (i < 0 || i >= size()) {
			throw new ArrayIndexOutOfBoundsException("index " + i
					+ " out of range. Should be [0," + size() + ")");
		}
		String k = positions.get(i);
		if (!values.containsKey(k)) {
			throw new RuntimeException("key " + k + " at position " + i
					+ " no present. Valid keys are " + this.keys());
		}
		return values.get(k);
	}
	
	public V getByName(String name) {
		if (!values.containsKey(name)) {
			throw new IllegalArgumentException(name + " not present"
					+ " available keys are " + values.keySet());
		}
		return values.get(name);
	}
	
	public int size() {
		return values.size();
	}
	
	public List<V> values() {
		List<V> list = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			list.add(getByIndex(i));
		}
		return list;
	}
	
	public List<String> keys() {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			list.add(positions.get(i));
		}
		return list;
	}
	
	public boolean has(String name) {
		return values.containsKey(name);
	}

	@Override
	public Iterator<V> iterator() {
		return values.values().iterator();
	}
}
