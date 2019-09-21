package com.nutanix.bpg.job.impl;

import java.lang.reflect.Array;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobExecutor;
import com.nutanix.bpg.job.Stage;

public class JobExecutorImpl implements JobExecutor {
	@Override
	public <S, T> CompletableFuture<?> execute(Job<S, T> job) {
		System.err.println("running " + job);

		CompletionService<S> ecs = new ExecutorCompletionService<S>(Executors.newCachedThreadPool());
		int N = job.getStages().size();
		@SuppressWarnings("unchecked")
		CompletableFuture<S>[] args = (CompletableFuture<S>[]) Array.newInstance(CompletableFuture.class, N);
		for (int i = 0; i < N; i++) {
			Stage<S, T> stage = job.getStages().get(i);
			try {
				Future<S> f = ecs.submit(stage);
				S value = f.get(stage.getExpectedCompletionTimeInMillis(), TimeUnit.MILLISECONDS);
				CompletableFuture.completedFuture(value);
				args[i] = CompletableFuture.completedFuture(value);
			} catch (Exception e) {
				throw Stage.convertError(e);
			}
		}
		return CompletableFuture.allOf(args);
	}
}
