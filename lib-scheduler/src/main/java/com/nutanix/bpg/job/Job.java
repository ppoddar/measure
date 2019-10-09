package com.nutanix.bpg.job;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nutanix.capacity.Capacity;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.resource.Resource;

/**
 * A job is a specification for execution of a script
 * in this framework.
 * <p>
 * A job is executed by a {@link JobExecutor#execute(Job)
 * executor}.
 * A job is scheduled for execution by {@link com.nutanix.bpg.scheduler.JobScheduler
 * scheduler}.
 * A job sits in a {@link JobQueue queue}.
 * A job and its status is communicated to browser
 * user via {@link JobToken token}.
 * 
 * 
 * 
 * @see JobScheduler
 * @see JobExecutor
 * @see JobQueue
 * 
 * @author pinaki.poddar
 *
 */

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, 
	include=JsonTypeInfo.As.PROPERTY,
	property="class")
public interface Job {
	
	public static enum Status {
		NOT_SCHEDULED, SCHEDULED, 
		RUNNING, 
		COMPLETED, CANCELLED, FAILED, 
		EXPIRED}
	
	String getId();
	String getName();
	String getCategory();
	long getStartTime();
	long getExpectedCompletionTimeInMillis();
	JobTemplate getTemplate();
	/**
	 * gets the demand (required resource capacity) 
	 * required by this job.
	 * @return a capacity never null
	 */
	Capacity getDemand();
	
	/**
	 * gets the supply (allocated resource capacity) 
	 * required by this job.
	 * @return a capacity never null
	 */
	Capacity getSupply();
	
	/**
	 * gets the resource allocated for this job
	 * @return
	 */
	Resource getResource();
	
	/**
	 * gets the submitter for the job
	 * @return
	 */
	String getSubmitter();

	void setName(String name);
	void setDesciptor(String desc);
	void setCategory(String cat);
	/**
	 * gets an estimate of time to complete this job
	 * execution.
	 * The expected completion time is sum of
	 * expected time of completion of all stages. 
	 * 
	 * @return time in millisecond.
	 */
	void setExpectedCompletionTimeInMillis(long ms);
	void setDemand(Capacity alloc);
	void setSupply(Resource rsrc, Capacity alloc);
	
	/**
	 * gets working directory in a remote machine
	 * where this job would be executed.
	 * <p>
	 * The working directory must exist in the 
	 * working directory.
	 * <p>
	 * 
	 * @return null if remote process executes in
	 * user's working directory.  
	 */
	String getWorkingDirectory();
	List<String> getCommand();
	Map<String, String> getEnvironment();
	/**
	 * arguments for script execution
	 * @return
	 */
	
}
