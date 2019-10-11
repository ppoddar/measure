package com.nutanix.job.execution;

import java.util.List;
import java.util.Map;

public class Command {
	private String[] tokens;
	
	public Command(String line) {
		tokens = line.split("\\s+");
	}
	
	
	public String[] replaceVariables(Map<String, String> vars) {
		String[] strings = new String[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			strings[i] = replaceVariables(tokens[i], vars);
		}
		
		return strings;
	}
	
	private String replaceVariables(String token, 
			Map<String, String> vars) {
		List<String> parsedVars = VariableParser.parse(token);
		for (String v : parsedVars) {
			if (!vars.containsKey(v)) {
				throw new RuntimeException("variable " + v
						+ " is not supplied for " + token
						+ " supplied variables are " + vars.keySet());
			}
			else {
				String marker = "${" + v + "}";
				token = token.replace(marker, vars.get(v));
			}
		}
		return token;
	}
	
	public String[] getTerms() {
		return tokens;
	}
}
