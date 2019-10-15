package com.nutanix.bpg.job;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nutanix.bpg.job.Job.Status;
import com.nutanix.bpg.job.impl.JobImpl;
import com.nutanix.bpg.job.impl.JobQueueImpl;

/**
 * a {@link JobToken} is a token for a {@link Job}.
 * <p>
 * A token is a promise to complete an execution
 * with an expiration period. 
 * The token can be queried to find about the sate
 * of a job.
 * 
 * 
 * @author pinaki.poddar
 *
 */
@JsonPropertyOrder({"name", "id", "category", "status",
	"queue", 
	"startTime", "expectedEndTime", "waitDuration",
	"ouputURI", "errorOutputURI",
	"errorMessage"
	})
public class JobToken {
	private JobImpl job;
	private String queue;
	private long startTime;
	private long waitTime;
	private long expectedExpirationTime;
	private CompletableFuture<?> promise;
	private Exception error;
	private Path root;
	private Path output;
	private Path errorOutput;
	private Status status;
	
	private static Logger logger = LoggerFactory.getLogger(JobToken.class);
	/**
	 * create a token.
	 * 
	 * @param job carries information about the job
	 */
	public JobToken(JobImpl job, JobQueueImpl queue) {
		if (job == null) {
			throw new IllegalArgumentException("can not create a token for null job");
		}
		this.job       = job;
		this.queue     = queue.getName();
		this.startTime = System.currentTimeMillis();
		try {
			this.root = queue.getOutputRoot();
			this.output    = queue.createPath("job-"+getId(), false);
			this.errorOutput = queue.createPath("job-"+getId(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * cancel underlying job, if possible.
	 */
	public void cancel() {
		if (this.promise == null) return;
		this.promise.cancel(true);
	}
	
	/**
	 * gets identifier of this token which is same as
	 * that of underlying {@link #getJob() Job}.
	 * @return 
	 */
	public String getId() {
		return job.getId();
	}

	/**
	 * gets name of this token which is same as
	 * that of underlying {@link #getJob() Job}.
	 * @return 
	 */
	public String getName() {
		return job.getName();
	}


	/**
	 * gets category of this token which is same as
	 * that of underlying {@link #getJob() Job}.
	 * @return 
	 */
	public String getCategory() {
		return job.getCategory();
	}
	
	/**
	 * get expected time when underlying {@link #getJob() Job}
	 * would be completed.
	 * 
	 * @return
	 */
	public long getExpectedEndTime() {
		return  job.getStartTime() 
			+   job.getExpectedCompletionTimeInMillis();
	}
	
	/**
	 * get start time when this token has been created.
	 * A token gets created when a job {@link JobQueue#addJob(Job) 
	 * enters} a job queue
	 * @return
	 */
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long t) {
		 startTime = t;
	}
	
	/**
	 * gets wait time of this token in job queue.
	 * wait time is difference between when a token
	 * enters a queue and when its job starts executing.
	 * @return
	 */
	public long getWaitDuration() {
		return job.getStartTime() - this.getStartTime();
	}
	
	@JsonIgnore
	public Object getResult() throws Exception {
		if (promise == null) return null;
		try {
			return promise.get();
		} catch (Exception ex) {
			return ex.toString();
		}
	}
	
	public void setStatus(Job.Status s) {
		//valiadteTransition(getStaus(), s);
		this.status = s;
	}
	/**
	 * returns latest status of the job.
	 * if promise is non-null, it's status is that of the promise.
	 * @return
	 */
	public Status getStatus() {
		if (error != null) {
			return Job.Status.FAILED;
		}
		if (promise != null) {
			if (promise.isCancelled()) {
				return  Job.Status.CANCELLED;
			} else if (promise.isCompletedExceptionally()) {
				return Job.Status.FAILED;
			} else if (promise.isDone()) {
				return Job.Status.COMPLETED;
			} else {
				return Job.Status.RUNNING;
			}
		} else {
			if (getJob().getSupplier() != null) {
				return Job.Status.SCHEDULED;
			} else if (status != null) {
				return status;
			}
		}
		return Job.Status.QUEUED;
	}
	
	@JsonIgnore
	public JobImpl getJob() {
		return job;
	}
	
	public String getQueue() {
		return queue;
	}
	
	public void setQueue(String queue) {
		this.queue = queue;
	}
	
	@JsonIgnore
	public CompletableFuture<?> getPromise() {
		return promise;
	}
	
	public void setPromise(CompletableFuture<?> promise) {
		this.promise = promise;
	}
	
	/**
	 * gets URI of job output w.r.t public root.
	 * @return
	 */
	public String getOutputURI() {
		return root.relativize(output).toString();
	}
	/**
	 * get path to local file of standard output of job execution,
	 * @return
	 */
	@JsonIgnore
	public Path getOutput() {
		return output;
	}
	@JsonIgnore
	public Path getErrorOutput() {
		return errorOutput;
	}
	
	public String getErrorMessage() {
		if (error != null) {
			return error.getMessage();
		}
		if (errorOutput != null) {
			try {
				return new String(Files.readAllBytes(errorOutput), Charset.defaultCharset());
			} catch (Exception ex) {
				return "can not read error output " + errorOutput;
			}
		}
		return "";
	}
	/**
	 * gets URI of job error output.
	 * @return
	 */
	public String getErrorOutputURI() {
		return root.relativize(errorOutput).toString();
	}
	public void setError(Exception ex) {
		this.error = ex;
	}
	
	

	public String toString() {
		return "job token [" + getName() + "] (status=" + getStatus() + ")";
	}
}
