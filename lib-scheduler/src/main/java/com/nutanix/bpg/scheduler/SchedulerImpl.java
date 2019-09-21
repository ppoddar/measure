package com.nutanix.bpg.scheduler;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobExecutor;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;

public class SchedulerImpl implements JobScheduler {
	JobQueue queue;
	JobExecutor executor;
	private  SchedulerImpl() {
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Collection<JobToken> tokens = queue.getJobs(Job.Status.INIT);
			for (JobToken t : tokens) {
				Job<?, ?> job = t.getJob();
				CompletableFuture<?> future = executor.execute(job);
				t.setPromise(future);
			}
			Thread.yield();;
		}
		
	}
}
