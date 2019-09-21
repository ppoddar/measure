package com.nutanix.bpg.job;

public interface JobQueueManager {

	JobQueue newQueue(String name);

	JobQueue getQueue(String name);

	<S,T> void addJob(JobQueue queue, Job<S,T> job);

}