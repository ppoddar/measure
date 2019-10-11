package com.nutanix.bpg.scheduler;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.resource.Allocation;

public class JobSchedulerImpl implements JobScheduler {
	private final JobQueue queue;
	private static long WAIT_MS = 1*1000;

	public JobSchedulerImpl(JobQueue queue) {
		this.queue = queue;
	}

	@Override
	public Void call() throws Exception {
		while (!Thread.currentThread().isInterrupted()) {
			if (queue.getPool() == null) {
				Thread.yield();
				continue;
			}
			for (JobToken token : queue) {
				switch (token.getStatus()) {
				case QUEUED:
					token.setStatus(Job.Status.SCHEDULED);
					Allocation alloc = queue.getPool().getAllocationPolicy().reserveAllocation(queue.getPool(),
							token.getJob().getDemand());
					if (alloc != null) {
						token.getJob().setSupplier(alloc.getSupply());
					}
					break;
				default:
				}
			}
			Thread.sleep(WAIT_MS);
		}
		return null;
	}
}
