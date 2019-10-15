package com.nutanix.bpg.job;

import java.util.concurrent.Callable;

public interface JobScheduler extends Callable<Void> {
	void process(JobToken token);
}
