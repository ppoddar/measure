package com.nutanix.bpg.job.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.Job.Status;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;


/**
 * a list of {@link Job jobs}.
 * 
 * @author pinaki.poddar
 *
 */
public class JobQueueImpl implements ThreadFactory, JobQueue {
	private String name;
	private final Map<String, JobToken> tokens;
	/**
	 * package scoped constructor 
	 */
	public JobQueueImpl() {
		tokens = new HashMap<String, JobToken>();
	}
	
	public JobQueue setName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public JobToken getJob(String id) {
		return tokens.get(id);
	}
	
	@Override
	public JobToken addJob(Job<?,?> job) {
		JobToken t = new JobToken(job, null)
				.setStatus(Job.Status.INIT);
		tokens.put(t.getId(), t);
		return t;
	}
	
	@Override
	public Collection<JobToken> getJobs(Status...statuses) {
		Collection<JobToken> result = new ArrayList<JobToken>();
		for (JobToken t : tokens.values()) {
			boolean seleced = false;
			if (statuses != null) {
				for (Status s : statuses) {
					if (t.getStatus() == s) {
						seleced = true;
						break;
					}
				}
			} else {
				seleced = true;
			}
			if (seleced) {
				result.add(t);
			}
		}
		return result;
	}
	

	@Override
	public Thread newThread(Runnable r) {
		return new Thread("Promise-"+tokens.size());
	}

	@Override
	public String getName() {
		return name;
	}

}
