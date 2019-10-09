package com.nutanix.bpg.scheduler;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobExecutorImpl;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.capacity.Capacity;
import com.nutanix.resource.Resource;

/**
 * A Job scheduler coordinates allocation of resources for a job, submission to
 * a queue and its subsequent execution.
 * 
 * @author pinaki.poddar
 *
 */
public class JobSchedulerImpl implements JobScheduler {
	private final JobQueue queue;
	private final Path outputRoot;
	private static final Logger logger = LoggerFactory.getLogger(JobSchedulerImpl.class);

	public JobSchedulerImpl(JobQueue q, Path root) {
		queue = q;
		outputRoot = root;
	}

	public String toString() {
		return "scheduler (queue-" + queue.getName() + ")";
	}

	/**
	 * scheduler schedules a job with given supply.
	 * 
	 */
	@Override
	public void schedule(JobToken token, Resource supply, Capacity demand) {

		logger.debug(this + " schedules " + token + " with " + supply);

		try {
			new JobExecutorImpl(token, outputRoot)
				.execute();
		} catch (Exception ex) {
			logger.debug("failed to execute asynchorous job " + token + " for following reason");
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			synchronized (queue) {
				try {
					logger.debug("waiting for " + queue);
					queue.wait();
					logger.debug(this + "selecting jobs from " + queue);
					List<JobToken> tokens = 
							queue.selectJobByStatus();
					logger.debug("selected " + tokens.size() + " jobs");
					for (JobToken token : tokens) {
						Job job = token.getJob();
						schedule(token, job.getResource(), job.getDemand());
					}
					Thread.yield();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					break;
				}
			}
		}
	}
	
}
