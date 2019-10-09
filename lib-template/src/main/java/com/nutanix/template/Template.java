package com.nutanix.template;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Template {
	private JsonNode data;
	public Template(JsonNode json) {
		this.data = json.deepCopy();
	}
	
	public String getName() {
		return data.get("name").asText();
	}
	
	public JsonNode instantiate(Map<String, String> vars) {
		return instantiateObject(this.data, vars);
	}
	
	public JsonNode instantiateObject(JsonNode json,
			Map<String, String> vars) {
		ObjectMapper mapper = new ObjectMapper();
		if (json.isValueNode()) return json;
		Iterator<String> fieldNames = json.fieldNames();
		ObjectNode instance = json.deepCopy();
		while (fieldNames.hasNext()) {
			String field = fieldNames.next();
			JsonNode value = json.get(field);
			if (value.isTextual()) {
				String text = value.asText();
				instance.replace(field, instance.textNode(replace(text, vars)));
			} else if (value.isObject()) {
				instance.set(field, instantiateObject(value, vars));
			} else if (value.isArray()) {
				instance.set(field, instantiateArray((ArrayNode)value, vars));
			} else {
				instance.set(field, value);
			}
		}
		return instance;
	}
	
	public ArrayNode instantiateArray(ArrayNode value,
			Map<String, String> vars) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode array = mapper.createArrayNode();
		for (JsonNode e : value) {
			if (e.isValueNode()) {
				if (e.isTextual()) {
					array.add(replace(e.asText(), vars));
				} else {
					array.add(e);
				}
			} else {
				array.add(instantiateObject(e, vars));
			}
		}
		return array;
	}
	
	String replace(String s, Map<String, String> vars) {
		return s;
	}
	
	
	public String write() {
		String s = "";
		s += this.data.get("command");
		String flag = this.data.get("option-flag").asText();
		JsonNode optionNodes = this.data.get("options");
		Iterator<String> options = optionNodes.fieldNames();
		while (options.hasNext()) {
			String option = options.next();
			String value = optionNodes.get(option).asText();
			s += " " + flag + option + " " + value;
		}
		return s;
	}
}
