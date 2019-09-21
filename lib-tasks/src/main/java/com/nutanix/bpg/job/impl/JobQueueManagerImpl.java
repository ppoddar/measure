package com.nutanix.bpg.job.impl;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobQueueManager;
import com.nutanix.bpg.model.Catalog;

public class JobQueueManagerImpl implements JobQueueManager {
	private Catalog<JobQueue> queues;
	private static JobQueueManager singleton;
	
	public static JobQueueManager instance() {
		if (singleton == null) {
			singleton = new JobQueueManagerImpl();
		} 
		return singleton;
	}
	
	private JobQueueManagerImpl() {
		queues = new Catalog<>();
	}
	
	@Override
	public JobQueue newQueue(String name) {
		JobQueue queue = new JobQueueImpl().setName(name);
		queues.add(queue);
		return queue;
	}
	
	@Override
	public JobQueue getQueue(String name) {
		return queues.get(name);
	}
	
	@Override
	public <S,T> void addJob(JobQueue queue, Job<S,T> job) {
		queue.addJob(job);
	}
}
