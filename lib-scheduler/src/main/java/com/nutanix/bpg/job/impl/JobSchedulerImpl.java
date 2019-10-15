package com.nutanix.bpg.job.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobScheduler;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.impl.DefaultAllocationPolicy;
import com.nutanix.resource.model.Cluster;

public class JobSchedulerImpl implements JobScheduler {
	private final JobQueue queue;
	private AllocationPolicy allocationPolicy
		= new DefaultAllocationPolicy();
		
	private static long WAIT_MS = 1*1000;
	private static Logger logger = LoggerFactory.getLogger(JobSchedulerImpl.class); 
	
	public JobSchedulerImpl(JobQueue queue) {
		this.queue = queue;
	}

	/**
	 * scheduler is called once a pool is available
	 */
	@Override
	public Void call() throws Exception {
		while (!Thread.currentThread().isInterrupted()) {
			for (JobToken token : queue) {
				switch (token.getStatus()) {
				case QUEUED:
					process(token);
					break;
				default:
					
				}
			}
			Thread.sleep(WAIT_MS);
		}
		return null;
	}
	
	@Override
	public void process(JobToken token) {
		logger.debug("process " + token);
		try {
			JobImpl job = token.getJob();
			if (job.getDemand() == null) {
				throw new IllegalArgumentException(job + " has no resource demand");
			}
			ResourcePool pool = queue.getPool();
			Allocation alloc = allocationPolicy
					.reserveAllocation(pool,
					job.getDemand(), null);
			if (alloc != null) {
				Cluster cluster = (Cluster)alloc.getSupplier();
				job.setSupplier(cluster);
				job.getSpecification().setOption("cluster", cluster.getHost());
//				job.getSpecification()
//				   .setOption("cluster", "10.46.31.26");
				
				token.setStatus(Job.Status.SCHEDULED);
			} else {
				logger.warn("can not allocate " + job.getDemand() + " for " + job);
			}
		} catch (Exception e) {
			token.setError(e);
			e.printStackTrace();
		}
	}
}
