package com.nutanix.bpg.job.impl;

import com.nutanix.bpg.job.DemandEstimator;
import com.nutanix.bpg.job.JobTemplate;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Storage;
import com.nutanix.capacity.impl.DefaultCapacity;

public class DefaultDemandEstimator implements DemandEstimator {

	@Override
	public Capacity estimateDemand(JobTemplate template) {
		Capacity demand = new DefaultCapacity();
		switch (template.getName().toLowerCase()) {
		case "nutest":
			demand.addQuantity(new Memory(100, MemoryUnit.MB));
			demand.addQuantity(new Storage(300, MemoryUnit.GB));
			break;
		default:
			throw new IllegalArgumentException("can not estimate demand for " + template);
		}
		
		return demand;
	}

}
