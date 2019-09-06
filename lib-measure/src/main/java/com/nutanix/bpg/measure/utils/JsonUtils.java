package com.nutanix.bpg.measure.utils;

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class JsonUtils {
	public static ObjectNode readResource(String rsrc) {
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(rsrc);
		if (in == null) {
			throw new IllegalArgumentException("resource [" + rsrc + "]"
					+ " not found to load a plugin");
		}
		return readStream(in);
	}
	
	public static ObjectNode readStream(InputStream in) {
		try {
			YAMLFactory yaml = new YAMLFactory();
			ObjectMapper mapper = new ObjectMapper(yaml);
			JsonNode json = mapper.readTree(in);
			return (ObjectNode)json;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void assertProperty(JsonNode json, String key) {
		assertProperty(json, key, false);
	}
	
	public static void assertProperty(JsonNode json, String key, boolean array) {
		if (!json.has(key)) {
			throw new IllegalArgumentException("missing property [" + key + "]");
		}
		if (array && !json.get(key).isArray()) {
			throw new IllegalArgumentException("property [" + key + "] is not an array");
		}
	}


}
