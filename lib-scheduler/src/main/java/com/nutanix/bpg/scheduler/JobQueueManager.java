package com.nutanix.bpg.scheduler;

import java.util.Collection;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.ResourcePoolSelectionPolicy;
import com.nutanix.job.execution.JobBuilder;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.resource.Resource;

/**
 * Manages, creates multiple queues.
 * 
 *
 */
public interface JobQueueManager {
	/**
	 * create a new job queue 
	 * @param name name of the job queue
	 * @return
	 */
	JobQueue newQueue(String name);
	
	JobScheduler addJob(JobQueue queue, 
			Job job, Resource supply) throws Exception;
	Collection<String> getQueues();
	
	/**
	 * gets a queue, creating if necessary.
	 * @param name name of queue
	 * @return a queue, never null
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