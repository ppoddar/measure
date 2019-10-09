package com.nutanix.bpg.job.impl;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.job.Task;

public class ParallelStage extends AbstractStage {
	
	public ParallelStage() {
		super(true);
	}
	
	public ParallelStage(ObjectMapper mapper, JsonNode json) {
		super(mapper, json);
	}

	public Boolean execute(CompletionService<Boolean> ecs) throws Exception{
		long timeout = getExpectedCompletionTimeInMillis();
		for (Task task : tasks) {
			ecs.submit(task);
			ecs.take().get(timeout, TimeUnit.MILLISECONDS);
		}
		return true;
	}

}
