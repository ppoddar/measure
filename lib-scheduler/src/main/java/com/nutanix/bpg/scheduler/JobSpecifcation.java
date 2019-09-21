package com.nutanix.bpg.scheduler;

import com.nutanix.capacity.Capacity;

public class JobSpecifcation {
	public Capacity getDemand() {
		throw new AbstractMethodError();
	}
	public String getPool() {
		throw new AbstractMethodError();
	}
}
