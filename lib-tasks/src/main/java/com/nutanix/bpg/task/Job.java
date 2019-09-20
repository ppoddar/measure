package com.nutanix.bpg.task;

import java.util.concurrent.TimeUnit;

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
public interface Job extends Iterable<Stage> {
	public static char JOB_SEQUNCE_SEPARTAOR  = '-';
	public static char JOB_PARRALEL_SEPARTAOR = '|';
	public static char JOB_OPEN_GROUP = '(';
	public static char JOB_CLOSE_GROUP = ')';
	
	String getName();
	String getDesciptor();
	int addStage(Stage stage);
	void execute(long time, TimeUnit unit);
}
