package com.nutanix.bpg.job;

import java.util.concurrent.CompletableFuture;

public interface JobExecutor {
	<S,T> CompletableFuture<?> execute(Job<S,T> job);
	
}
