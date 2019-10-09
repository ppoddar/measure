package com.nutanix.bpg.job.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.Stage;
import com.nutanix.bpg.job.Task;
import com.nutanix.bpg.utils.StringUtils;

public abstract class AbstractStage implements Stage {
	private String name;
	private boolean parallel;
	private long expectedTimeInMillis = -1;
	List<Task> tasks = new ArrayList<Task>();
	
	private static Executor threadPool = Executors.newCachedThreadPool();
	private static CompletionService<Boolean> ecs
		= new ExecutorCompletionService<Boolean>(threadPool);
	
	protected AbstractStage(boolean p) {
		parallel = p;
	}
	
	protected AbstractStage(ObjectMapper mapper, JsonNode json) {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * affirms if tasks in this stage are executed in parallel.
	 * 
	 * @return true if tasks are executed in parallel
	 */
	boolean isParallel() {
		return parallel;
	}
	/**
	 * gets an estimate of time to complete this stage of execution. The expected
	 * time of completion is either sum or maximum of expected time of completion of
	 * all tasks for sequential and parallel execution respectively.
	 * 
	 * @return time in millisecond.
	 */
	@JsonIgnore
	@JsonProperty(required = false)
	public long getExpectedCompletionTimeInMillis() {
		if (expectedTimeInMillis < 0) {

			double time = 0;
			if (parallel) {
				time = Double.NEGATIVE_INFINITY;
				for (Task t : tasks) {
					time = Math.max(time, t.getExpectedCompletionTimeInMillis());
				}
			} else {
				for (Task t : tasks) {
					time += t.getExpectedCompletionTimeInMillis();
				}
			}
			expectedTimeInMillis = (long) time;
		}
		return expectedTimeInMillis;
	}

	public int addTask(Task t) {
		tasks.add(t);
		return tasks.size();
	}

	public List<Task> getTasks() {
		return tasks;
	}
	

	public String toString() {
		char sep = isParallel() ? Job.TASK_SEPARTAOR_PARRALEL : Job.TASK_SEPARTAOR;
		return OPEN_GROUP + StringUtils.join(sep, tasks) + CLOSE_GROUP;
	}


	@Override
	public Boolean call() throws Exception {
		return execute(ecs);
	}
	
	protected abstract Boolean execute(CompletionService<Boolean> ecs)
		throws Exception;


}
