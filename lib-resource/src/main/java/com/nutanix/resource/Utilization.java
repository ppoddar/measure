package com.nutanix.resource;

import java.util.Map;

import com.nutanix.resource.Resource.Kind;

public interface Utilization {
	void put(Resource.Kind kind, Double d);
	Utilization accumulate(Map<Kind, Double> b);
	Utilization accumulate(Utilization other);
	
}
