package com.nutanix.bpg.job;

import java.util.List;

import com.nutanix.capacity.Capacity;

/**
 * A job is a specification for execution of a set of
 * {@link Task tasks}. 
 * A job executes {@link Stage stage} sequentially.
 * Each {@Stage stage} is a group of tasks that execute
 * either in sequence or in parallel.
 * <p>
 * A Job is represented as stages separated by <code>-</code>
 * symbol. Whereas a stage is list of task enclosed in 
 * <code>()</code> and tasks are separated by <code>|</code>
 * or <code>-</code> based on if a stage is parallel or not.
 * <p>
 * <pre>
 * 	  (A)-(B|C)-(D-E)-(F)
 * </pre>
 * 
 * @see JobExecutor
 * 
 * @author pinaki.poddar
 *
 */
public interface Job<S,T> {
	
	public static enum Status {INIT, ALLOCATED, SCHEDULED, 
		RUNNING, COMPLETED, CANCELLED, FAILED}
	public static char TASK_SEPARTAOR          = '-';
	public static char TASK_SEPARTAOR_PARRALEL = '|';
	
	String getId();
	String getName();
	void setName(String name);
	String getDesciptor();
	void setDesciptor(String desc);
	String getCategory();
	void setCategory(String cat);
	long getStatrtTime();
	void getStatrtTime(long ms);
	/**
	 * gets an estimate of time to complete this job
	 * execution.
	 * The expected completion time is sum of
	 * expected time of completion of all stages. 
	 * 
	 * @return time in millisecond.
	 */
	long getExpectedCompletionTimeInMillis();
	long setExpectedCompletionTimeInMillis(long ms);
	
	
	int addStage(Stage<S,T> stage);
	
	void setDemand(Capacity alloc);
	
	List<Stage<S, T>> getStages();
	
	
}
