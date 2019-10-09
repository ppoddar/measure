package com.nutanix.bpg.scheduler;

import java.util.Collection;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.ResourcePoolSelectionPolicy;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.job.execution.JobBuilder;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.resource.Resource;

public interface JobQueueManager {
	JobQueue newQueue(String name, Repository repo);
	JobScheduler addJob(JobQueue queue, Job job, Resource supply);
	Collection<String> getQueues();
	
	/**
	 * gets a queue creating if necessary.
	 * @param name
	 * @return
	 */
	JobQueue getQueue(String name);
	
	/**
	 * gets builder to build a job 
	 * @return
	 */
	JobBuilder getJobBuilder();
	/**
	 * gets job template by name
	 * @param name
	 * @return
	 */
	JobTemplate getJobTemplate(String name);
	
	ResourcePoolSelectionPolicy getResourcePoolSelectionPolicy();
}