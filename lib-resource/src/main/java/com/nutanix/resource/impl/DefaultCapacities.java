package com.nutanix.resource.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Resource.Kind;

public class DefaultCapacities implements Capacities {
	private Map<Resource.Kind, Capacity>
		capacities = new HashMap<Resource.Kind, Capacity>();
	
	
	public DefaultCapacities() {
		
	}
	public DefaultCapacities(Collection<Capacity> capacities) {
		for (Capacity c : capacities) {
			addCapacity(c);
		}
	}
	
	public DefaultCapacities(Capacities other) {
		for (Capacity c : other) {
			addCapacity(c);
		}
	}
	
	
	@Override
	public void reduceCapacities(Capacities other) {
		for (Capacity c : other) {
			reduceCapacity(c);
		}
	}

	@Override
	public boolean hasKind(Kind kind) {
		return capacities.containsKey(kind);
	}

	@Override
	public Capacity getCapacity(Kind kind) {
		return capacities.get(kind);
	}

	@Override
	public void addCapacity(Capacity cap) {
		if (hasKind(cap.getKind())) {
			cap = cap.plus(this.getCapacity(cap.getKind()));
		}
		capacities.put(cap.getKind(), cap);
	}
	@Override
	public Iterator<Capacity> iterator() {
		return capacities.values().iterator();
	}
	
	@Override
	public Set<Kind> getKinds() {
		return capacities.keySet();
	}
	
	@Override
	public void addCapacities(Capacities cap) {
		for (Capacity c : cap) {
			addCapacity(c);
		}
	}
	
	public String toString() {
		String s = "";
		Iterator<Capacity> capacities = iterator();
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
		result = prime * result + ((capacities == null) ? 0 : capacities.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!Capacities.class.isInstance(obj))
			return false;
		Capacities other = Capacities.class.cast(obj);
		for (Capacity c : this) {
			if (!other.hasKind(c.getKind())) 
				return false;
			if (!other.getCapacity(c.getKind()).equals(c)) {
				return false;
			}
		}
		for (Capacity c : other) {
			if (!this.hasKind(c.getKind())) 
				return false;
		}
		return true;
	}
	@Override
	public void reduceCapacity(Capacity cap) {
		if (this.hasKind(cap.getKind())) {
			cap = this.getCapacity(cap.getKind()).minus(cap);
			this.capacities.put(cap.getKind(), cap);
		} 
	}
	
	

}
