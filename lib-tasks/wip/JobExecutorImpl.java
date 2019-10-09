package com.nutanix.bpg.job.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobExecutor;
import com.nutanix.bpg.job.Stage;

public class JobExecutorImpl implements JobExecutor {
	private static Logger logger = LoggerFactory.getLogger(JobExecutor.class);

	/**
	 * executes a job in sequential stages. 
	 * returns a future that would evaluate to
	 * false if no stages are present. Otherwise, 
	 * applies each stage sequentially.
	 */
	@Override
	public CompletableFuture<Boolean> execute(Job job) {
		if (job.getStages().isEmpty()) {
			logger.debug("job " + job + " has no stages. skipping");
			return CompletableFuture.completedFuture(false);
		}
		Executor e = Executors.newCachedThreadPool();
		CompletionService<Boolean> ecs
        = new ExecutorCompletionService<Boolean>(e);
		
		CompletableFuture<Boolean> result = CompletableFuture.completedFuture(true);
		for (Stage stage : job.getStages()) {
			try {
				ecs.submit(stage).get();
			} catch (Exception ex) {
				logger.warn("error apply stage " + stage + " with exception:" +  ex);
				result.completeExceptionally(ex);
				result.complete(false);
				return result;
			}
		}
		return result;
	}
}