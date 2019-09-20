package com.nutanix.bpg.task;

public class TaskImpl implements Task {
	String name;
	
	public TaskImpl() {
		
	}
	public TaskImpl(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String call() throws Exception {
		return "running " + this;
	}
	
	
}
