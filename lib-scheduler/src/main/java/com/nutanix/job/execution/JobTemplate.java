package com.nutanix.job.execution;
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
 * A job template is a YML specification 
 * for script execution.
 * A template specifies the command and its
 * arguments, the working directory and env
 * variables.
 * 
 * The command arguments can be variables with 
 * <code>$(..)</code> syntax.
 * <br>
 * The runtime context must supply the value of
 * all variables specified in a template.
 *  
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class JobTemplate implements Named {
	private JsonNode data;
	private static Logger logger = LoggerFactory.getLogger(JobTemplate.class);
	
	/**
	 * creates a template from given JSON descriptor.
	 * the command options can also be specified
	 * with variable values.
	 * 
	 * @param json must have 'name' and 'command'
	 */
	public JobTemplate(JsonNode json) {
		if (json == null) {
			throw new IllegalArgumentException("can not build template from null data");
		}
		JsonUtils.assertProperty(json, "name");
		JsonUtils.assertProperty(json, "command");
		this.data = json.deepCopy();
	}
	
	/**
	 * gets name of this template. A name identifies
	 * a template.
	 */
	public String getName() {
		return data.get("name").asText();
	}
	
	public String getDirectory() {
		String txt = this.data.get("directory").asText();
		txt = txt.replaceFirst("~", System.getProperty("user.home"));
		return txt;
	}
	/**
	 * gets command for this Job template.
	 * Typically a single string, but can be more
	 * e.g. python <some.py>
	 * @return 
	 */
	public List<String> getCommand() {
		String s = this.data.get("command").asText();
		List<String> list = new ArrayList<String>(Arrays.asList(s.split(" ")));
		
		return list;
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
	public Map<String, String> getCommandOptions() {
		Map<String, String> options = JsonUtils.getMap(data, "options", Collections.emptyMap());
		return options;
	}
	
	/**
	 * command options are specified with variables.
	 * The given variable replacements are applied
	 * to create a list of options that can be 
	 * directly applied.
	 * 
	 * @param vars a set of replacements. All variables
	 * mentioned in the options must be present in the
	 * given map.
	 * 
	 * @return a list of options ready to be applied
	 * wit script
	 */
	public List<String> fillCommandOptions(
			Map<String, String> vars) {
		List<String> result = new ArrayList<>();
		String flag = JsonUtils.getString(this.data,"option-flag","");
		
		Map<String, String> options = getCommandOptions();
		for (String key : options.keySet()) {
			String value = options.get(key);
			String replaced = replaceOption(key, value, vars);
			result.add(flag + key);
			result.add(replaced);
		}
		if (result.isEmpty()) {
			logger.warn("no option for " + this);
		}
		return result;
	}

	/**
	 * replace variable with given replacement
	 * @param key name of the options. used for reporting
	 * @param value of the option as in template 
	 * i.e may use variables
	 * @param vars replacement variables for option value
	 * @return
	 */
	private String replaceOption(String key, String value, Map<String, String> vars) {
		String result = value;
		List<String> variableNames = VariableParser.parse(value);
		if (variableNames.isEmpty()) {
			logger.debug("option [" + key + "] with value [" + value + "] is not using any variable");
			return result;
		}
		logger.debug("option [" + key + "] with value [" + value + "] " 
				+ " is using " + variableNames.size()
				+ " variables: " + variableNames
				+ " they would be replaced by " + vars);
		for (String var : variableNames) {
			if (vars.containsKey(var)) {
				String regex = "\\$\\{" + var + "\\}";
				if (!vars.containsKey(var)) {
					throw new IllegalArgumentException("option [" + key + "] with value [" + value + "]"
							+ " is using variables " + variableNames
							+ " however, supplied variable values " + vars
							+ " does not contain value for variable  [" + var + "]");
				}
				String replacement = vars.get(var);
				result = result.replaceAll(regex, replacement);
			} else {
				throw new RuntimeException("undefined variable [" + var + "]"
						+ " in option " + value);
			}
		}
		return result;
	}
	
	public String toString() {
		return "job template:" + getName();
	}

}
