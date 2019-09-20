package com.nutanix.resource.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.nutanix.resource.Capacity;
import com.nutanix.resource.Quantity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Resource.Kind;

public class DefaultCapacity implements Capacity {
	private Map<Resource.Kind, Quantity>
		quantities = new HashMap<Resource.Kind, Quantity>();
	
	
	public DefaultCapacity() {
		
	}
	public DefaultCapacity(Collection<Quantity> capacities) {
		for (Quantity c : capacities) {
			addQuantity(c);
		}
	}
	
	public DefaultCapacity(Capacity other) {
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
	public boolean hasKind(Kind kind) {
		return quantities.containsKey(kind);
	}

	@Override
	public Quantity getQuantity(Kind kind) {
		return quantities.get(kind);
	}

	@Override
	public void addQuantity(Quantity q2) {
		if (hasKind(q2.getKind())) {
			q2 = q2.plus(this.getQuantity(q2.getKind()));
		}
		quantities.put(q2.getKind(), q2);
	}
	@Override
	public Iterator<Quantity> iterator() {
		return quantities.values().iterator();
	}
	
	@Override
	public Set<Kind> getKinds() {
		return quantities.keySet();
	}
	
	@Override
	public void addCapacities(Capacity cap) {
		for (Quantity c : cap) {
			addQuantity(c);
		}
	}
	
	public String toString() {
		String s = "";
		Iterator<Quantity> capacities = iterator();
		while (capacities.hasNext()) {
			s += capacities.next();
			s += capacities.hasNext() ? "," : "";
		}
		return s;
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
			if (!other.hasKind(c.getKind())) 
				return false;
			if (!other.getQuantity(c.getKind()).equals(c)) {
				return false;
			}
		}
		for (Quantity c : other) {
			if (!this.hasKind(c.getKind())) 
				return false;
		}
		return true;
	}
	@Override
	public void reduceQuantity(Quantity cap) {
		if (this.hasKind(cap.getKind())) {
			cap = this.getQuantity(cap.getKind()).minus(cap);
			this.quantities.put(cap.getKind(), cap);
		} 
	}
	@Override
	public Capacity convert() {
		Capacity result = new DefaultCapacity();
		for (Quantity q : quantities.values()) {
			result.addQuantity(q);
		}
		return result;
	}
	
	

}
