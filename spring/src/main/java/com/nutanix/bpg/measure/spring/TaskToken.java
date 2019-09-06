package com.nutanix.bpg.measure.spring;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * a {@link TaskToken} is a {@link CompletableFuture
 * promise} to be fulfilled.
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class TaskToken<T>  {
	private final String id;
	private final String name;
	private final String category;
	private final long startTime;
	private final long expectedDuration;
	private final CompletableFuture<T> promise;
	private static Logger logger = LoggerFactory.getLogger(TaskToken.class);

	/**
	 * create a token with given promise.
	 * 
	 * @param obj a promise. It must notify when 
	 * complete.
	 */
	public TaskToken(String name, 
			String category,
			long expectedDuration,
			CompletableFuture<T> p) {
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
