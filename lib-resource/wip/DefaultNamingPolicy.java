package com.nutanix.resource.model;

public class DefaultNamingPolicy implements NamingPolicy {

	@Override
	public String toJavaClassName(String jsonName) {
		String result = toJavaName(jsonName);
		return capitalize(result);
	}
	@Override
	public String toJavaFieldName(String jsonName) {
		String result = toJavaName(jsonName);
		return result;
	}
	
	public String toJavaName(String jsonName) {
		String result = "";
		for (int i = 0; i < jsonName.length(); i++) {
			int code = jsonName.codePointAt(i);
			char ch = jsonName.charAt(i);
			if (Character.isAlphabetic(code)) {
				if (i > 0 && jsonName.charAt(i-1) == '_')
					ch = Character.toUpperCase(ch);
				result += ch;
			}
		}
		return result;
	}

	@Override
	public String toJsonName(String javaName) {
		String result = "";
		for (int i = 0; i < javaName.length(); i++) {
			char ch = javaName.charAt(i);
			if (Character.isUpperCase(ch)) {
				result += '-' + Character.toUpperCase(ch);
			} else {
				result += ch;
			}
		}
		return result;
	}

	@Override
	public String toSingular(String name) {
		if (name.endsWith("ies")) {
			return name.substring(0, name.length()-3) + "y";
		} else if (name.endsWith("s")) {
			return name.substring(0, name.length()-3);
		} else {
			return name;
		}
	}
	
	public String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0))
				+ s.substring(1);
	}

}
