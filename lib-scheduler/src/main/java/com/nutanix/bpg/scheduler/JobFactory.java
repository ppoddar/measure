package com.nutanix.bpg.scheduler;

import com.nutanix.bpg.job.Job;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.ResourcePool;

public class JobFactory {
	private Allocation allocation;
	private ResourcePool pool;
	
	public JobFactory withPool(ResourcePool pool) {
		this.pool = pool;
		return this;
	}
	public JobFactory withAllocation(Allocation alloc) {
		this.allocation = alloc;
		return this;
	}
	
	public Job createJob() {
		throw new AbstractMethodError();
	}
	
}
