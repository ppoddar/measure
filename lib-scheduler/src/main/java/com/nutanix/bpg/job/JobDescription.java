package com.nutanix.bpg.job;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.utils.JsonUtils;

/**
 * Job description contains information about a job
 * that are specified by the user/submitter.
 * <br>
 * A {@link JobTemplate template} contains information
 * about a job that can be specified at design time.
 * <br>
 * A {@link JobBuilder builder} combines these two 
 * sources of information, to produce a {@link Job job}
 * ready for allocation and execution, i.e.
 * <pre>
 *    Job Description -----
 *                         |
 *                        Job Builder --> Job
 *                         |
 *    Job Template    ------
 * </pre>
 * 
 */
public class JobDescription {
	String name;
	String category;
	String description;
	String submitter;
	Map<String, String> optionValues;
	Map<String, String> env;
	
	public JobDescription() {
		category = "";
		name     = "";
		description = "";
		submitter = "";
		optionValues = new HashMap<>();
		env = new HashMap<>();
	}
	
	public JobDescription(JsonNode json) {
		category  = JsonUtils.getString(json, "category");
		name      = JsonUtils.getString(json, "name");
		submitter = JsonUtils.getString(json, "submitter", "");
		optionValues = JsonUtils.getMap(json, "options", new HashMap<String, String>());
		env = JsonUtils.getMap(json, "env", new HashMap<String, String>());
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	
	public String getSubmitter() {
		return submitter;
	}
	
	public Map<String, String> getOptionValues() {
		return optionValues;
	}
	
	public Map<String, String> getEnvironment() {
		return env;
	}
	
	
	public void setOption(String key, String value) {
		optionValues.put(key, value);
	}
}
