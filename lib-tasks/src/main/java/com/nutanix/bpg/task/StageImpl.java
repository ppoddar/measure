package com.nutanix.bpg.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.nutanix.bpg.utils.StringUtils;

public class StageImpl implements Stage {
	final String name;
	final boolean parallel;
	private long time = 1;
	private TimeUnit unit = TimeUnit.SECONDS;
	
	List<Task> tasks = new ArrayList<Task>();
	
	public StageImpl() {
		this(false);
	}
	
	public void setTimeout(long time, TimeUnit unit) {
		this.time = time;
		this.unit = unit;
	}
	public StageImpl(boolean parallel) {
		this("no name", parallel);
	}
	public StageImpl(String name) {
		this(name, false);
	}
	
	public StageImpl(String name, boolean parallel) {
		this.name = name;
		this.parallel = parallel;
	}

	@Override
	public Iterator<Task> iterator() {
		return tasks.iterator();
	}

	@Override
	public boolean isParallel() {
		return parallel;
	}
	
	public String toString() {
		char sep = isParallel() ? Job.JOB_PARRALEL_SEPARTAOR : Job.JOB_SEQUNCE_SEPARTAOR;
		return Job.JOB_OPEN_GROUP 
				+ StringUtils.join(sep, tasks) 
				+ Job.JOB_CLOSE_GROUP;
	}

	@Override
	public int addTask(Task t) {
		tasks.add(t);
		return tasks.size();
	}
	
	@Override
	public void run() {
		ExecutorCompletionService<String> service
			= new ExecutorCompletionService<String>(Executors.newCachedThreadPool());
		for (Task task: this) {
			Future<String> future = service.submit(task);
			if (isParallel()) {
				continue;
			} else {
				try {
					future.get(time, unit);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					throw new RuntimeException(e.getCause());
				} catch (TimeoutException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}

}
