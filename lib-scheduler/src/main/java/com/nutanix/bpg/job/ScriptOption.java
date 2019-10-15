package com.nutanix.bpg.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.utils.JsonUtils;

public class ScriptOption {
	private String key;
	private String value;
	private boolean requiresValue;
	
	public ScriptOption() {
		
	}
	
	public ScriptOption(JsonNode json) {
		key = JsonUtils.getString(json, "key");
		requiresValue = json.has("value");
		if (requiresValue) {
			value = JsonUtils.getString(json, "value");
		}
	}
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	@JsonProperty("requires-value")
	public boolean requiresValue() {
		return requiresValue;
	}
	
	public String toString() {
		return "option [" + getKey() + ": " + getValue() + "]";
	}
}
