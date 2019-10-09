package com.nutanix.capacity;

import java.util.Map;

public interface Utilization extends Iterable<ResourceKind> {
	void put(ResourceKind kind, Double d);
	
	/**
	 * gets statistics 
	 * @param kind
	 * @return
	 */
	Statistics get(ResourceKind kind);
	Utilization accumulate(Utilization other);
	double getAverage();
	double getWeightedAverage(Map<ResourceKind,Double> weights);
}
