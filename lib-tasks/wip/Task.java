package com.nutanix.bpg.job;

import java.util.concurrent.Callable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A task is atomic unit of Job Execution framework.
 * A task can throw exception.
 * 
 * @author pinaki.poddar
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS,
    include=JsonTypeInfo.As.PROPERTY,
    property="class")
public interface Task extends Callable<Boolean> {
	/**
	 * gets expected time of completion for this task.
	 * @return
	 */
	public long getExpectedCompletionTimeInMillis();

}
