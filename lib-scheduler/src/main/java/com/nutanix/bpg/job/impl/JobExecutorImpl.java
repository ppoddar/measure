package com.nutanix.bpg.job.impl;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobBuilder;
import com.nutanix.bpg.job.JobExecutor;
import com.nutanix.bpg.job.JobQueueManager;
import com.nutanix.bpg.job.JobTemplate;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.job.execution.ScriptExecutor;

public class JobExecutorImpl implements JobExecutor {
	private final JobQueueImpl queue;
	private static Logger logger = LoggerFactory.getLogger(JobExecutorImpl.class);
	private static long WAIT_MS = 1*1000;
	/**
	 * an executor requires a {@link JobToken token}
	 * that provides the {@link Job job} to execute
	 * and root w.r.t. which all output|error paths
	 * are relativized.
	 * 
	 * @param t
	 * @param root
	 * @throws IOException
	 */
	public JobExecutorImpl(JobQueueImpl queue) {
		this.queue = queue;
	}
	
	/**
	 * executes given job in a remote process. standard and error output of remote
	 * process are found in two remote files.
	 * 
	 */
	@Override
	public Void call() throws Exception {
		while (!Thread.currentThread().isInterrupted()) {
			for (JobToken token : queue) {
					Job.Status status = token.getStatus();
					if (status != Job.Status.SCHEDULED) {
						continue;
					}
					execute(token);
			}
			Thread.sleep(WAIT_MS);
		}
		return null;
	}
	
	/**
	 * executes job associated with given token 
	 */
	void execute(JobToken token) {
		try {
			JobImpl job = token.getJob();
			JobQueueManager ctx = queue.getManager();
			JobTemplate template = ctx
					.getJobTemplate(job.getSpecification().getCategory());
			JobBuilder builder   = ctx.getJobBuilder();
			builder.setJobCommand(template, (JobImpl)job);

			logger.debug("=============> executing " + token);
			if (job.getCommand().isEmpty()) {
				throw new IllegalStateException("" + job + " has no command");
			}
			
			ScriptExecutor script = new ScriptExecutor()
					.withDirectory(job.getWorkingDirectory().toFile())
					.withEnvironment(job.getEnvironment())
					.withOutput(token.getOutput().toFile())
					.withErrorOutput(token.getErrorOutput().toFile())
					.withCommand(job.getCommand());

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					job.setStartTime(System.currentTimeMillis());
					try {
						Exception ex = script.call();
						if (ex != null) {
							token.setError(ex);
						} 
					} catch (Exception ex) {
						logger.debug("job execution error below ");
						ex.printStackTrace();
						token.setError(ex);
					}
				}
			};
			CompletableFuture<?> promise = CompletableFuture.runAsync(runnable);
			token.setPromise(promise);
		} catch (Exception ex) {
			token.setError(ex);
			ex.printStackTrace();
		}
	}
}
