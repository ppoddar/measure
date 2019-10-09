package com.nutanix.bpg.scheduler;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;

public class JobCleaner implements Runnable {
	private final JobQueue queue;
	public static Logger logger =  LoggerFactory.getLogger(JobCleaner.class);
	public JobCleaner(JobQueue q) {
		queue = q;
	}

	@Override
	public void run() {
		Iterator<JobToken> tokens = queue.selectJobByStatus(
				Job.Status.CANCELLED, 
				Job.Status.COMPLETED,
				Job.Status.EXPIRED).iterator();
		while (tokens.hasNext()) {
			JobToken token = tokens.next();
			logger.warn("removing " + token + " from " + queue);
			tokens.remove();
		}
	}
}
