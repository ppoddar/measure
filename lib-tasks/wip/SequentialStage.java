package com.nutanix.bpg.job.impl;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.Stage;
import com.nutanix.bpg.job.Task;
import com.nutanix.bpg.utils.StringUtils;

public class SequentialStage extends AbstractStage {
	private static Logger logger = LoggerFactory.getLogger(Stage.class);

	public SequentialStage() {
		super(false);
	}
	public SequentialStage(ObjectMapper mapper, JsonNode json) {
		super(mapper, json);
	}
	
	@Override
	protected Boolean execute(CompletionService<Boolean> ecs) throws Exception {
		logger.debug("get result of serial " + tasks.size() + " tasks");
		for (Task task : tasks) {
			logger.debug("submit task " + task);
			Future<Boolean> f = ecs.submit(task);
			logger.debug("get result from task " + task);
			f.get(task.getExpectedCompletionTimeInMillis(), TimeUnit.MILLISECONDS);
		}
		return true;
	}
	

}
