package com.nutanix.bpg.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;


/**
 * a list of {@link Task tasks}.
 * 
 * @author pinaki.poddar
 *
 */
public class TaskQueue implements ThreadFactory {
	private final Map<String, TaskToken> tokens;
	/**
	 * package scoped constructor 
	 */
	TaskQueue() {
		tokens = new HashMap<String, TaskToken>();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public TaskToken getTask(String id) {
		return tokens.get(id);
	}
	
	public TaskToken addTask(
			String name, 
			String category, 
			long end,
			CompletableFuture<?> promise) {
		TaskToken t = new TaskToken(name, category, end, promise);
		tokens.put(t.getId(), t);
		return t;
	}
	
	public Collection<TaskToken> getTasks() {
		return tokens.values();
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread("Promise-"+tokens.size());
	}

}
