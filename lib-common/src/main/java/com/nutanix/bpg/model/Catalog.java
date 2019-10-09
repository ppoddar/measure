package com.nutanix.bpg.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.nutanix.bpg.utils.Named;
import com.nutanix.bpg.utils.NamedMap;

/**
 * a map of {@link Named named} instances indexed by name.
 * 
 * @author pinaki.poddar
 *
 * @param <T> type of elements
 */

public class Catalog<T extends Named> implements Iterable<T> {
	private String name;
	private final boolean strict;
	private final NamedMap<T> map;
	
	/**
	 * creates a catalog where keys are case insensitive
	 * and lookup is not strict i.e. if key is not found
	 * null would be returned
	 */
	public Catalog() {
		this(true, true);
	}

	/**
	 * creates a catalog where keys have given caseinsensitivity
	 * and lookup is not strict i.e. if key is not found
	 * null would be returned
	 */
	public Catalog(boolean caseinsensitive) {
		this(caseinsensitive, true);
	}
	
	/**
	 * creates a catalog where keys have given caseinsensitivity
	 * and lookup strictness
	 */
	public Catalog(boolean caseinsensitive, boolean strict) {
		this.strict = strict;
		map = new NamedMap<>(caseinsensitive);
	}

	
	/**
	 * adds an identifiable element to this receiver.
	 * @param t element
	 */
	public void add(T t) {
		if (t == null) {
			throw new IllegalArgumentException("can not add null element");
		}
		if (t.getName() == null || t.getName().isEmpty()) {
			throw new IllegalArgumentException("can not add element " +  t + " with null/empty name");
		}
		map.add(t);
	}
	
	public void addAll(List<T> ts) {
		for (T t : ts) add(t);
	}
	public void addAll(T[] ts) {
		for (T t : ts) add(t);
	}
	public void addAll(Catalog<T> catalog) {
		for (T t : catalog) add(t);
	}
	
	/**
	 * gets an identifiable by its id
	 * @param name name of an element
	 * @return can be null if not strict
	 */
	public T get(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("can not lookup by empty/null key"
					+ " available keys are " + map.names());
		}
		if (map.containsKey(name)) {
			return map.get(name);
		} else if (strict) {
			throw new IllegalArgumentException("key [" + name + "] not found"
					+ " available keys are " + map.names());
		} else {
			return null;
		}
	}
	
	/**
	 * affirms if this receiver contains given identity
	 * @param name name of an element
	 * @return true if given name exists
	 */
	public boolean has(String name) {
		return map.containsKey(name);
	}

	/**
	 * gets all elements in this receiver
	 * @return all values
	 */
	public Collection<T> values() {
		return map.values();
	}
	public Collection<String> names() {
		return map.names();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public Iterator<T> iterator() {
		return values().iterator();
	}
	
	public String toString() {
		return "catalog:" + getName();
	}
	
	public int size() {
		return map.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
