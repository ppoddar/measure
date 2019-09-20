package com.nutanix.bpg.measure.utils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class JsonUtils {
	
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
	
	public static JsonNode assertProperty(JsonNode json, String key) {
		assertProperty(json, key, false);
		return json.get(key);
	}
	
	public static JsonNode assertProperty(JsonNode json, String key, boolean array) {
		if (!json.has(key)) {
			throw new IllegalArgumentException("missing property [" + key + "]");
		}
		if (!json.get(key).isArray()) {
			throw new IllegalArgumentException("property [" + key + "] is not an array");
		}
		return json.get(key);
	}
	/**
	 * reads a JSON/YAML file from given url.
	 * 
	 * @param url if no protocol then interprets as 
	 * file system path relative to current working directory.
	 * 
	 * @return
	 */
	public static JsonNode readResource(URL url, boolean yaml) {
		try {
			ObjectMapper mapper = yaml
				? new ObjectMapper(new YAMLFactory())
				: new ObjectMapper();
			return mapper.readTree(url.openStream());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}



}
