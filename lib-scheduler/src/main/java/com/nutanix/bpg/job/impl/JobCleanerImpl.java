package com.nutanix.bpg.job.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobCleaner;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;

public class JobCleanerImpl implements JobCleaner {
	private final JobQueue queue;
	public static Logger logger = LoggerFactory.getLogger(JobCleanerImpl.class);

	public JobCleanerImpl(JobQueue q) {
		queue = q;
	}

	@Override
	public Void call() throws Exception {
		while (Thread.currentThread().isInterrupted()) {
			Iterator<JobToken> tokens = queue.iterator();
			while (tokens.hasNext()) {
				JobToken token = tokens.next();
				Job.Status status = token.getStatus();
				switch (status) {
				case CANCELLED:
				case COMPLETED:
				case EXPIRED:
					logger.debug("removing " + token);
					tokens.remove();
					break;
				default:
				}
			}
		}
		return null;
	}

}
