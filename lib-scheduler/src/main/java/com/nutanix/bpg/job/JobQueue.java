package com.nutanix.bpg.job;

import com.nutanix.bpg.utils.Named;
import com.nutanix.resource.ResourcePool;

/**
 * A queue of {@link JobToken job token}.
 *
 */
public interface JobQueue extends Named, Iterable<JobToken> {
	
	 JobQueueManager getManager();
	/**
	 * the pool associated to this queue.
	 * @return
	 */
	ResourcePool getPool();
	/**
	 * sets one side of bi-directional 1:1 relationship
	 * between a {@link JobQueue} and {@link ResourcePool}.
	 * 
	 * @param pool a resource pool
	 * @return the same receiver
	 */
	JobQueue setPool(ResourcePool pool);
	
	/** 
	 * gets token of given identifier
	 * @param id an identifier
	 * @return can be null if no such token
	 */
	JobToken getJob(String id);

	
	/**
	 * adds given job to this queue. 
	 * 
	 * @param job a non-null job
	 * @return a token that wraps the job.
	 * The token does not have  a promise
	 * @exception IllegalStateException if given job
	 * lacks requisite information to be executed
	 * as a script
	 */
	JobToken addJob(Job job)  throws Exception;
}