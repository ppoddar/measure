package com.nutanix.bpg.measure.script;

import java.util.ArrayList;
import java.util.List;

public class VariableParser {
	public static List<String> parse(String s) {
		StringBuffer var = new StringBuffer();
		List<String> vars = new ArrayList<String>();
		boolean collectingVar = false;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '{') {
				if (!collectingVar) {
					if (i > 0 && s.charAt(i - 1) == '$') {
						collectingVar = true;
					} else {
						var.append(ch);
					}
				}
			} else if (ch == '}') {
				if (collectingVar) {
					collectingVar = false;
					vars.add(var.toString().trim());
					var = new StringBuffer();
				} else {
					var.append(ch);
				}
			} else {
				if (collectingVar) {
					var.append(ch);
				}
			}
		}
		return vars;
	}
}
