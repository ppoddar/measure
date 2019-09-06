package com.nutanix.bpg.measure.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a list of {@link Task tasks}.
 * 
 * @author pinaki.poddar
 *
 */
public class TaskQueue implements ThreadFactory {
	Map<String, TaskToken<?>> tokens = 
			new HashMap<String, TaskToken<?>>();
	private ExecutorService pool = 
			Executors.newCachedThreadPool(this);
	private static Logger logger = LoggerFactory.getLogger(TaskQueue.class);

	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public TaskToken<?> getTask(String id) {
		return tokens.get(id);
	}
	
	public <T> TaskToken<T> addTask(
			String name, 
			String category, 
			long end,
			CompletableFuture<T> promise) {
		TaskToken<T> t = new TaskToken<T>(name, category, end, promise);
		tokens.put(t.getId(), t);
		return t;
	}
	
	public Collection<TaskToken<?>> getTasks() {
		return tokens.values();
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread("Promise-"+tokens.size());
	}

}
