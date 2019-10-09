package com.nutanix.bpg.job;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nutanix.bpg.job.Job.Status;
import com.nutanix.bpg.scheduler.JobImpl;

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
	
	private static Logger logger = LoggerFactory.getLogger(JobToken.class);
	/**
	 * create a token.
	 * 
	 * @param job carries information about the job
	 */
	public JobToken(JobImpl job) {
		if (job == null) {
			throw new IllegalArgumentException("can not create a token for null job");
		}
		this.job       = job;
		this.startTime = System.currentTimeMillis();
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
	
	/**
	 * returns latest status of the job.
	 * if promise is non-null, it's status is that of the promise.
	 * @return
	 */
	public Status getStatus() {
		Job.Status status = null;
		if (error != null || hasErrorOutput()) {
			return Job.Status.FAILED;
		}
		long now = System.currentTimeMillis();
		if (getExpectedEndTime() > 0 
		 && getExpectedEndTime()>now) {
			return Job.Status.EXPIRED;
		}
		if (promise == null) {
			return  Job.Status.NOT_SCHEDULED;
		} 
		if (promise.isCancelled()) {
			status = Job.Status.CANCELLED;
		} else if (promise.isCompletedExceptionally()) {
			status = Job.Status.FAILED;
		} else if (promise.isDone()) {
			status = Job.Status.COMPLETED;
		} else {
			status = Job.Status.RUNNING;
		}
		return status;
	}
	
	public boolean hasErrorOutput() {
		return errorOutput != null
			&& errorOutput.toFile().length() > 0;
		
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
	 * gets URI of job output.
	 * @return
	 */
	public String getOutputURI() {
		if (output == null) {
			logger.warn("token output not set");
			return "";
		}
		if (root == null) {
			logger.warn("root output dir not set");
			return "";
		}
		return root.relativize(output).toString();
	}
	
	@JsonIgnore
	public File getOutputFile() {
		if (output == null) {
			throw new IllegalStateException("token output not set");
		}
		return output.toFile();
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
		if (errorOutput == null) {
			logger.warn("token error output not set");
			return "";
		}
		if (root == null) {
			logger.warn("root output dir not set");
			return "";
		}
		return root.relativize(errorOutput).toString();
	}
	@JsonIgnore
	public File getErrorOutputFile() {
		if (errorOutput == null) {
			throw new IllegalStateException("token error output not set");
		}
		return errorOutput.toFile();
	}

	public void setOutput(Path path) {
		if (path == null) {
			throw new IllegalArgumentException("can not set null output path");
		}
		output = path;
	}
	
	public void setErrorOutput(Path err) {
		if (err == null) {
			throw new IllegalArgumentException("can not set null error output path");
		}
		errorOutput = err;
	}

	public void setError(Exception ex) {
		this.error = ex;
	}
	
	
	/**
	 * sets the path against which all output|error
	 * URI is resolved
	 * 
	 * @param r an output path w.r.t which all output|error
	 * URI
	 */
	public void setRoot(Path r) {
		if (r == null) {
			throw new IllegalArgumentException("can not set null output root path");
		}
		if (!r.toFile().exists()) {
			throw new IllegalArgumentException("can not set non-existent output root " + r.toUri());
		}
		if (!r.toFile().isDirectory()) {
			throw new IllegalArgumentException("can not set non-directory output root " + r.toUri());
		}
		this.root = r;
	}

	public String toString() {
		return "job token-" + getId() + ":" + getName();
	}

}
