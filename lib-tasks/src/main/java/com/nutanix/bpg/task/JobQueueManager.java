package com.nutanix.bpg.task;

import java.util.HashMap;
import java.util.Map;

public class JobQueueManager {
	private Map<String, TaskQueue> queues;
	
	private static JobQueueManager singleton;
	
	public static JobQueueManager instance() {
		if (singleton == null) {
			singleton = new JobQueueManager();
		} 
		return singleton;
	}
	
	private JobQueueManager() {
		queues = new HashMap<String, TaskQueue>();
	}
	
	public TaskQueue newQueue(String name) {
		TaskQueue queue = new TaskQueue();
		queues.put(name, queue);
		return queue;
	}
	
	public TaskQueue getQueue(String name) {
		return queues.get(name);
	}
}
