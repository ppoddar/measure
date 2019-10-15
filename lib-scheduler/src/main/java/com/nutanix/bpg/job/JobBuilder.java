package com.nutanix.bpg.job;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.impl.DefaultDemandEstimator;
import com.nutanix.bpg.job.impl.JobImpl;
import com.nutanix.capacity.Capacity;
import com.nutanix.job.execution.VariableParser;

public class JobBuilder {
	private static Logger logger = LoggerFactory.getLogger(JobBuilder.class);
	/**
	 * a job submission is partially completed by
	 * input information.
	 * <p>
	 * The allocation information is not filled.
	 * 
	 * @param template to use for this job
	 * @param descriptor job description
	 * @return
	 */
	public JobImpl build(
			JobTemplate template, 
			JobDescription jobSpec) {
		logger.debug("creating Job from " + template);
		JobImpl job = new JobImpl(template, jobSpec);
		
		DemandEstimator estimator = new DefaultDemandEstimator();
		Capacity demand = estimator.estimateDemand(template);
		// demand must be set of a job to qualify for scheduling
		job.setDemand(demand);
		
		Map<String, String> env = template.getEnvironment();
		env.putAll(job.getSpecification().getEnvironment());
		job.setEnvironment(env);
		return job;
	}
	
	public void setJobCommand(JobTemplate template, 
			Job job) {
		List<String> commands = fillTemplate(job.getSpecification(), template);
		job.setCommand(commands);
	}
	
	/**
	 * replaces any variable in a {@link JobTemplate template}
	 * by {@link JobDescription#getOptionValues() values}.
	 * <br>
	 * All template variables must be supplied.
	 *  
	 * @param desc a descriptor carries variable values
	 * @param template is defined in terms of variables
	 * 
	 * @return the command for execution of the job. The
	 * variable are replaced by value.
	 */
	private List<String> fillTemplate(JobDescription desc, JobTemplate template) {
		List<String> commands    = template.getCommand();
		for (ScriptOption option: template.getScriptOptions()) {
			commands.add(template.getOptionFlag() + option.getKey());
			if (!option.requiresValue()) continue;
			
			String resolved = replaceOption(option, desc.getOptionValues());
			commands.add(resolved);
			
		}
		return commands;
	}
	
	/**
	 * replace variable with given replacement
	 * @param key name of the options. used for reporting
	 * @param value of the option as in template 
	 * i.e may use variables
	 * @param vars replacement variables for option value
	 * @return
	 */
	private String replaceOption(ScriptOption option, Map<String, String> vars) {
		String value  = option.getValue();
		List<String> variableNames = VariableParser.parse(value);
		if (variableNames.isEmpty()) {
			return value;
		}
		String result = value;
		for (String var : variableNames) {
			if (vars == null) {
				throw new IllegalArgumentException(option
						+ " requires variables " + variableNames
						+ " but null variables were supplied");
			}
			if (!vars.containsKey(var)) {
				throw new IllegalArgumentException(option 
						+ " is using variables " + variableNames
						+ " However, supplied variable values " + vars
						+ " does not contain value for variable  [" + var + "]");
			}
			String regex = "\\$\\{" + var + "\\}";
			String replacement = vars.get(var);
			result = result.replaceAll(regex, replacement);
			 
		}
		return result;
	}

}
