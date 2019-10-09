package com.nutanix.bpg.scheduler;

import com.nutanix.bpg.job.JobToken;
import com.nutanix.capacity.Capacity;
import com.nutanix.resource.Resource;

public interface JobScheduler extends Runnable{
	void schedule(JobToken token, 
			Resource supply, Capacity demand);
	
}
