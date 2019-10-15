package com.nutanix.bpg.job;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.Named;
/**
 * A job template is a design-time specification 
 * for job execution.
 * <br>
 * A job execution is essentially execution of 
 * OS script -- a script can be a Java or Python 
 * wrapped in OS command.
 * <br>
 * A template specifies 
 * <ul>
 *   <li>the script command
 *   <li>{@link ScriptOption script option}
 *   <li>working directory 
 *   <li>env variables.
 * <br>
 * The script arguments can be variables with 
 * <code>$(..)</code> syntax. 
 * <br>
 * These variables
 * are substitued by value before Job execution.
 * The runtime context <b>must</b> supply the value of
 * all variables specified in a template.
 *
 */
public class JobTemplate implements Named {
	private String name;
	private List<String> command;
	private Path workingDirectory;
	private String optionFlag;
	private List<ScriptOption> options;
	private Map<String, String> env;
	
	private static Logger logger = LoggerFactory.getLogger(JobTemplate.class);
	
	/**
	 * creates a template from given JSON descriptor.
	 * the command options can also be specified
	 * with variable values.
	 * 
	 * @param json must have 'name' and 'command'
	 * rest are optional.
	 */
	public JobTemplate(JsonNode json) {
		if (json == null) {
			throw new IllegalArgumentException("can not build template from null data");
		}
		JsonUtils.assertProperty(json, "name");
		JsonUtils.assertProperty(json, "command");
		
		
		setName(JsonUtils.getString(json, "name"));
		setCommand(JsonUtils.getString(json, "command"));
		String dir = JsonUtils.getString(json, "directory", "");
		setWorkingDirectory(dir.replaceFirst("~", System.getProperty("user.home")));
		setOptionFlag(JsonUtils.getString(json, "option-flag", ""));
		options = new ArrayList<>();
		for (JsonNode e : json.at("/options")) {
			options.add(new ScriptOption(e));
		}
		env = JsonUtils.getMap(json, "env", Collections.emptyMap());
		
		logger.debug("created " + this);
		logger.debug("\tcommand=" + getCommand());
		logger.debug("\tdirectory=" + getWorkingDirectory());
		
		
	}
	
	/**
	 * gets name of this template. A name identifies
	 * a template.
	 */
	public String getName() {
		return name;
	}
	
	void setName(String name) {
		this.name = name;
	}
	void setOptionFlag(String flag) {
		this.optionFlag = flag;
	}
	
	void setCommand(String cmds) {
		this.command = new ArrayList<String>();
		this.command.addAll(Arrays.asList(
				cmds.split(" ")));
	}
	
	public Path getWorkingDirectory() {
		return workingDirectory;
	}
	
	public void setWorkingDirectory(String dir) {
		Path path = Paths.get(dir);
		if (!path.toFile().exists()) {
			throw new IllegalArgumentException("working directory " + path + " for " + this + " does not exist");
		}
		if (!path.toFile().isDirectory()) {
			throw new IllegalArgumentException("working directory " + path + " for " + this + " is not directory");
		}
		workingDirectory = path;
	}
	
	public Map<String, String> getEnvironment() {
		return env;
	}
	
	/**
	 * gets command for this Job template.
	 * Typically a single string, but can be more
	 * e.g. python <some.py>
	 * @return 
	 */
	public List<String> getCommand() {
		return command;
	}
	
	public String getOptionFlag() {
		return optionFlag;
	}

	/**
	 * 
	 * 
	 * @return  map for command options. 
	 * The key represents an option name.
	 * The value is value for the option. The value
	 * may represent a variable such as 
	 * <code>${cluster}</code>
	 * 
	 */
	public List<ScriptOption> getScriptOptions() {
		return options;
	}
	
	public String toString() {
		return "job template:" + getName();
	}
}
