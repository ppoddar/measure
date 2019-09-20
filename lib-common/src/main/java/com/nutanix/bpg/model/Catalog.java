package com.nutanix.bpg.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.nutanix.bpg.utils.NamedMap;

/**
 * a repository of generic type indexed by 
 * @author pinaki.poddar
 *
 * @param <T> type of elements
 */

public class Catalog<T extends Named> implements Iterable<T> {
	private String name;
	private NamedMap<T> map = new NamedMap<>();
	
	/**
	 * create an unnamed, empty catalog
	 */
	public Catalog() {
	}
	
	/**
	 * adds an identifiable element to this receiver.
	 * @param t element
	 */
	public void add(T t) {
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
	 * @return can be null
	 */
	public T get(String name) {
		return map.get(name);
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
