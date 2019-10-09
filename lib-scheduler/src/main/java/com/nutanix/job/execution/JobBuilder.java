package com.nutanix.job.execution;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.scheduler.JobImpl;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.KeyValueParser;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.CapacityFactory;

public class JobBuilder {
	private static Logger logger = LoggerFactory.getLogger(JobBuilder.class);
	/**
	 * a job submission is partially completed by
	 * input information.
	 * <p>
	 * The allocation information is not filled.
	 * 
	 * @param descriptor job description
	 * @param template to use for this job
	 * @param option values
	 * @return
	 */
	public JobImpl build(JobTemplate template, 
			JsonNode descriptor, Map<String, String> optionValues) {
		logger.debug("creating Job from " + template);
		JobImpl job = new JobImpl(template);
		
		String name = JsonUtils.getString(descriptor, "name", "");
		logger.debug("creating Job name: [" + name + "]");
		job.setName(name);

		String category = JsonUtils.getString(descriptor, "category", "");
		logger.debug("creating Job category: [" + category + "]");
		job.setCategory(category);
		
		Map<String, String> quantities = JsonUtils.getMap(descriptor, "demand");
		Capacity demand    = CapacityFactory.newCapacity(quantities);
		logger.debug("creating Job demand: [" + demand + "]");
		job.setDemand(demand);

		String submitter = JsonUtils.getString(descriptor, "submitter", "");
		logger.debug("creating Job submitter: [" + submitter + "]");
		job.setSubmitter(submitter);

		Map<String, String> env   = KeyValueParser.parse(
				JsonUtils.getString(descriptor, "env", ""));
		logger.debug("creating Job env: [" + env + "]");
		job.setEnv(env);
		
		fillAllocation(job, template, optionValues);
		return job;
	}
	
	public void fillAllocation(JobImpl job, 
			JobTemplate template,
			Map<String, String> optionValues) {
		Map<String,String> options = template.getCommandOptions();
		logger.debug(template + " has " + options.size() + " command options " + options);
		List<String> commands    = template.getCommand();
		List<String> commandArgs = template.fillCommandOptions(optionValues);
		logger.debug("commad arguments " + commandArgs);
		commands.addAll(commandArgs);
		logger.debug("creating Job command: [" + commands + "]");
		job.setCommand(commands);
		logger.debug("full commad with arguments " + commands);
	}
	
	
	
	
}
