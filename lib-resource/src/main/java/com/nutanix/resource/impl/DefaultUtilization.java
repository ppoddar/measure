package com.nutanix.resource.impl;

import java.util.HashMap;
import java.util.Map;

import com.nutanix.resource.Resource;
import com.nutanix.resource.Utilization;
import com.nutanix.resource.Resource.Kind;

public class DefaultUtilization implements Utilization {
	Map<Resource.Kind, Double> data
		= new HashMap<Resource.Kind, Double>();
	
	
	public void put(Resource.Kind kind, Double d) {
		data.put(kind, d);
	}
	
	public Utilization accumulate(Map<Kind, Double> b) {
		for (Resource.Kind k : b.keySet()) {
			this.put(k, (this.data.containsKey(k)) 
				? this.data.get(k) + b.get(k)
				: b.get(k));
		}
		return this;
	}

	@Override
	public Utilization accumulate(Utilization other) {
		// TODO Auto-generated method stub
		return null;
	}


}
