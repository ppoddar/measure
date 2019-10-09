package com.nutanix.bpg.job;

import java.util.List;

import com.nutanix.bpg.job.Job.Status;
import com.nutanix.bpg.scheduler.JobScheduler;
import com.nutanix.bpg.utils.Named;

public interface JobQueue extends Named, Iterable<JobToken> {
	
	JobScheduler getScheduler();
	/**
	 * @param id
	 * @return
	 */
	JobToken getJob(String id);

	
	/**
	 * adds given job to this queue. 
	 * 
	 * @param job
	 * @return a token without a promise
	 * 
	 */
	JobToken addJob(Job job);

	/**
	 * gets the jobs with given status. 
	 * 
	 * @param statuses set of statuses. 
	 * null implies all given statuses
	 * @return all jobs with given status
	 */
	List<JobToken> selectJobByStatus(Status... statuses);

}