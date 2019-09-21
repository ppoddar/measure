package com.nutanix.capacity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DefaultUtilization implements Utilization {
	Map<ResourceKind, Double> data;
	public DefaultUtilization() {
		data = new HashMap<ResourceKind, Double>();
		for (ResourceKind k : ResourceKind.values()) {
			data.put(k, 0.0);
		}
	}

	public void put(ResourceKind kind, Double d) {
		data.put(kind, d);
	}
	
	public Utilization accumulate(Map<ResourceKind, Double> b) {
		for (ResourceKind k : b.keySet()) {
			this.put(k, (this.data.containsKey(k)) 
				? this.getKind(k) + b.get(k)
				: b.get(k));
		}
		return this;
	}

	@Override
	public Utilization accumulate(Utilization other) {
		for (ResourceKind k : other) {
			this.put(k, (this.data.containsKey(k)) 
				? this.getKind(k) + other.getKind(k)
				: other.getKind(k));
		}
		return this;
	}

	@Override
	public Iterator<ResourceKind> iterator() {
		return data.keySet().iterator();
	}

	@Override
	public double getKind(ResourceKind kind) {
		return data.get(kind);
	}


}
