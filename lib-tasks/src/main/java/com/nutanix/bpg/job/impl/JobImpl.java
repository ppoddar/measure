package com.nutanix.bpg.job.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.Stage;
import com.nutanix.bpg.utils.StringUtils;
import com.nutanix.capacity.Capacity;

public class JobImpl<S,T> implements Job<S,T>, ThreadFactory {
	private String id;
	private String name;
	private String category;
	private long startTime;
	private Capacity demand;
	private int counter;
	
	List<Stage<S,T>> stages = new ArrayList<Stage<S,T>>();
	
	public JobImpl() {
		id = UUID.randomUUID().toString();
		startTime = System.currentTimeMillis();
	}
	
	public String getId() {
		return id;
	}
	
	protected Job<S,T> setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	@Override
	public List<Stage<S,T>> getStages() {
		return stages;
	}

	@Override
	public int addStage(Stage<S,T> stage) {
		stages.add(stage);
		return stages.size();
	}
	
	
	
	@Override
	public String toString() {
		return "job:" + getName();
	}

	@Override
	public String getDesciptor() {
		return StringUtils.join(TASK_SEPARTAOR, stages);
	}
	
	
	

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(this.getName() + "-" + counter++);
	}

		
	

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public long getStatrtTime() {
		return startTime;
	}

	@Override
	public long getExpectedCompletionTimeInMillis() {
		long sum = 0;
		for (Stage<S,T> stage : stages) {
			sum += stage.getExpectedCompletionTimeInMillis();
		}
		return sum;
	}

	@Override
	public void setDesciptor(String desc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCategory(String cat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getStatrtTime(long ms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long setExpectedCompletionTimeInMillis(long ms) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDemand(Capacity capacity) {
		this.demand = capacity;
	}



}
