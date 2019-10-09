package com.nutanix.bpg.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final String name;
	private JobScheduler scheduler;
	private final Map<String, JobToken> tokens;
	private static Logger logger = LoggerFactory.getLogger(JobQueueImpl.class);
	/**
	 * package scoped constructor 
	 */
	public JobQueueImpl(String name) {
		this.name = name;
		this.tokens = new HashMap<String, JobToken>();
	}
	
	public void setScheduler(JobScheduler s) {
		this.scheduler = s;
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
	public JobToken addJob(Job job) {
		logger.debug("adding " + (tokens.size()) + "-th " + job + " to " + this); 
		JobToken t = new JobToken((JobImpl)job);
		tokens.put(t.getId(), t);
		t.setQueue(name);
		synchronized (this) {
			notifyAll();
		}
		return t;
	}
	
	@Override
	public List<JobToken> selectJobByStatus(Status...statuses) {
		List<JobToken> result = new ArrayList<JobToken>();
		for (JobToken t : tokens.values()) {
			boolean seleced = statuses == null || statuses.length == 0;
			if (statuses != null) {
				for (Status s : statuses) {
					if (t.getStatus() == s) {
						seleced = true;
						break;
					}
				}
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

	@Override
	public Iterator<JobToken> iterator() {
		return tokens.values().iterator();
	}

	@Override
	public JobScheduler getScheduler() {
		return scheduler;
	}
	
	public String toString() {
		return "queue-" + getName();
	}
	

}
