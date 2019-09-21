package com.nutanix.capacity;

import java.util.Map;

public interface Utilization extends Iterable<ResourceKind>{
	void put(ResourceKind kind, Double d);
	double getKind(ResourceKind kind);
	Utilization accumulate(Map<ResourceKind, Double> b);
	Utilization accumulate(Utilization other);
	
}
