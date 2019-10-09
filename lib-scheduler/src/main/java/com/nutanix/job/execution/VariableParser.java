package com.nutanix.job.execution;

import java.util.ArrayList;
import java.util.List;

/**
 * parses a string for variables.
 * A variable is designated by enclosing a string
 * in <code>${...}</code> marker.
 * 
 * @author pinaki.poddar
 *
 */
public class VariableParser {
	public static List<String> parse(String s) {
		StringBuffer var = new StringBuffer();
		List<String> vars = new ArrayList<String>();
		boolean collectingVar = false;
		for (int i = 1; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '$':
				if (collectingVar) {
					var.append(ch);
				}
				break;
			case '{':
				if (collectingVar) {
					var.append(ch);
				} else if (i > 0 && s.charAt(i-1) == '$') {
					collectingVar = true;
				}
				break;
			case '}':
				if (collectingVar) {
					collectingVar = false;
					vars.add(var.toString());
				} 
				break;
			default:
				if (collectingVar) {
					var.append(ch);
				}
				break;
			}
			
		}
		return vars;
	}
}
