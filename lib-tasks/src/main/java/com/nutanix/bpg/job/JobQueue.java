package com.nutanix.bpg.job;

import java.util.Collection;

import com.nutanix.bpg.job.Job.Status;
import com.nutanix.bpg.utils.Named;

public interface JobQueue extends Named {
	/**
	 * 
	 * @param id
	 * @return
	 */
	JobToken getJob(String id);

	/**
	 * add a job. 
	 * 
	 * @param job
	 * @return a token without a promise
	 * 
	 */
	JobToken addJob(Job<?,?> job);

	/**
	 * gets the jobs with given status. 
	 * 
	 * @param statuses set of statuses. 
	 * null implies all given statuses
	 * @return all jobs with given status
	 */
	Collection<JobToken> getJobs(Status... statuses);

}