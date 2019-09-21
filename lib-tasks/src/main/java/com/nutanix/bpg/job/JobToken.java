package com.nutanix.bpg.job;

import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.bpg.job.Job.Status;

/**
 * a {@link JobToken} is a token for a {@link Job}.
 * The token can be queried to find about the sate
 * of a job.
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class JobToken {
	private final Job<?,?> job;
	private Status status;
	private CompletableFuture<?> promise;

	/**
	 * create a token with given promise.
	 * 
	 * @param obj a promise. It must notify when 
	 * complete.
	 */
	public JobToken(Job<?,?> job,
			CompletableFuture<?> p) {
		this.job        = job;
		this.promise   = p;
	}

	public void cancel() {
		this.promise.cancel(true);
	}
	
	
	public String getId() {
		return job.getId();
	}

	
	public String getCategory() {
		return job.getCategory();
	}
	
	public long getExpectedEndTime() {
		return  job.getStatrtTime() 
				+ job.getExpectedCompletionTimeInMillis();
	}
	
	public String getName() {
		return job.getName();
	}

	public long getStartTime() {
		return job.getStatrtTime();
	}
	
	@JsonIgnore
	public Object getResult() throws Exception {
		return promise.get();
	}
	
	public Status getStatus() {
		if (promise == null) {
			status = Job.Status.INIT;
			return status;
		}
		if (promise.isCancelled()) {
			status = Job.Status.CANCELLED;
		} else if (promise.isCompletedExceptionally()) {
			status = Job.Status.FAILED;
		} else if (promise.isDone()) {
			status = Job.Status.COMPLETED;
		}
		return status;
	}
	
	public JobToken setStatus(Status status) {
		this.status = status;
		return this;
	}
	
	public Job<?, ?> getJob() {
		return job;
	}
	public CompletableFuture<?> getPromise() {
		return promise;
	}
	public void setPromise(CompletableFuture<?> promise) {
		this.promise = promise;
	}

}
