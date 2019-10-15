package com.nutanix.bpg.job;

import com.nutanix.capacity.Capacity;

public interface DemandEstimator {
	Capacity estimateDemand(JobTemplate template);
}
