package com.nutanix.bpg.task;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * a {@link TaskToken} is a {@link CompletableFuture
 * promise} to be fulfilled.
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class TaskToken {
	private final String id;
	private final String name;
	private final String category;
	private final long startTime;
	private final long expectedDuration;
	private final CompletableFuture<?> promise;

	/**
	 * create a token with given promise.
	 * 
	 * @param obj a promise. It must notify when 
	 * complete.
	 */
	public TaskToken(String name, 
			String category,
			long expectedDuration,
			CompletableFuture<?> p) {
		this.id        = UUID.randomUUID().toString();
		this.name      = name;
		this.category  = category;
		this.promise   = p;
		this.startTime = System.currentTimeMillis();
		this.expectedDuration = expectedDuration;
	}

	public void cancel() {
		this.promise.cancel(true);
	}
	
	
	public String getId() {
		return id;
	}

	public String getState() {
		String state = "RUNNING";
		if (promise.isCancelled()) {
			state = "CANCELLED";
		} else if (promise.isDone()) {
			state = "DONE";
		}
		return state;
	}
	
	public String getCategory() {
		return category;
	}
	
	public long getExpectedDuration() {
		return expectedDuration;
	}
	
	public String getName() {
		return name;
	}

	public long getStartTime() {
		return startTime;
	}
	
	@JsonIgnore
	public Object getResult() throws Exception {
		return promise.get();
	}
}
