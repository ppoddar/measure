package com.nutanix.capacity.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Unit;

/**
 * 
 * @author pinaki.poddar
 *
 */
public class DefaultCapacity implements Capacity {
	private Map<ResourceKind, Quantity> quantities;
	private Map<ResourceKind, Unit> preferredUnits;
	
	/**
	 * creates empty capacity. 
	 * an empty capacity has zero quantity of each {@link ResourceKind kind}
	 * 
	 * 
	 */
	public DefaultCapacity() {
		quantities     = new HashMap<>();
		preferredUnits = new HashMap<>();
	}
	
	/**
	 * creates capacity from given quantities
	 * @param capacities
	 */
	public DefaultCapacity(Collection<Quantity> capacities) {
		this();
		for (Quantity c : capacities) {
			addQuantity(c);
		}
	}
	
	/**
	 * copy constructor
	 * @param other
	 */
	public DefaultCapacity(Capacity other) {
		this();
		for (Quantity c : other) {
			addQuantity(c);
		}
	}
	
	
	
	
	@Override
	public void reduceCapacity(Capacity other) {
		for (Quantity c : other) {
			reduceQuantity(c);
		}
	}


	@Override
	public Quantity getQuantity(ResourceKind kind) {
		if (!quantities.containsKey(kind)) {
			throw new RuntimeException(this + " does not have " + kind);
		}
		return quantities.get(kind);
	}

	/**
	 * adds given quantity.
	 * 
	 */
	@Override
	public void addQuantity(Quantity q2) {
		ResourceKind kind = q2.getKind();
		if (!quantities.containsKey(kind)) {
			this.quantities.put(kind, q2);
		} else {
			this.quantities.put(kind, this.getQuantity(kind).plus(q2));
		}
	}
	
	@Override
	public Iterator<Quantity> iterator() {
		return quantities.values().iterator();
	}
	
//	@Override
//	public Set<ResourceKind> getKinds() {
//		return quantities.keySet();
//	}
	
	@Override
	public void addCapacity(Capacity cap) {
		for (Quantity c : cap) {
			addQuantity(c);
		}
	}
	
	public String toString() {
		String s = "capacity-{";
		Iterator<Quantity> quantities = iterator();
		while (quantities.hasNext()) {
			s += quantities.next();
			s += quantities.hasNext() ? "," : "";
		}
		return s + "}";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((quantities == null) ? 0 : quantities.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!Capacity.class.isInstance(obj))
			return false;
		Capacity other = Capacity.class.cast(obj);
		for (Quantity c : this) {
			if (!other.getQuantity(c.getKind()).equals(c)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void reduceQuantity(Quantity cap) {
		cap = this.getQuantity(cap.getKind()).minus(cap);
		this.quantities.put(cap.getKind(), cap);
	}

	@Override
	public void setPreferredUnit(ResourceKind kind, Unit unit) {
		preferredUnits.put(kind, unit);
	}

	@Override
	public Unit getPreferredUnit(ResourceKind kind) {
		return preferredUnits.get(kind);
	}
	
	

}
