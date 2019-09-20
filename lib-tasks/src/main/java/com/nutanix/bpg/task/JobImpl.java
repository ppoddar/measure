package com.nutanix.bpg.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.nutanix.bpg.utils.StringUtils;

public class JobImpl implements Job, ThreadFactory {
	private String name;
	List<Stage> stages = new ArrayList<Stage>();
	
	public JobImpl() {
		this("no name");
	}
	
	public JobImpl(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public Iterator<Stage> iterator() {
		return stages.iterator();
	}

	@Override
	public int addStage(Stage stage) {
		stages.add(stage);
		return stages.size();
	}
	
	
	
	@Override
	public String toString() {
		return "job:" + getName();
	}

	@Override
	public String getDesciptor() {
		return StringUtils.join(
				Job.JOB_SEQUNCE_SEPARTAOR, stages);
	}
	
	private int counter;
	
	

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(this.getName() + "-" + counter++);
	}

	@Override
	public void execute(long time, TimeUnit unit) {
		System.err.println("running " + this);
		Iterator<Stage> stages = this.iterator();
		CompletableFuture<?> future = 
				CompletableFuture.runAsync(new Runnable() {
					public void run() {}});
		// run each stage sequentially, 
		// each stage runs asynchronously within a timeout
		while (stages.hasNext()) {
			Stage stage = stages.next();
			stage.setTimeout(time, unit);
			future = future.thenRun(stage);
			try {
				future.get(time, unit);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				Throwable t = e.getCause();
				if (RuntimeException.class.isInstance(t)) {
					throw RuntimeException.class.cast(t);
				} else {
					throw new RuntimeException(t);
				}
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}



}
