package com.nutanix.bpg.job;

import java.util.concurrent.Callable;

/**
 * A task is atomic unit of Job Execution framework.
 * A task can throw exception.
 * 
 * @author pinaki.poddar
 *
 */
public interface Task<T> extends Callable<T> {
	/**
	 * gets expected time of completion for this task.
	 * @return
	 */
	long getExpectedCompletionTimeInMillis();
}
