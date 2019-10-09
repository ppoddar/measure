package com.nutanix.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.utils.ResourceUtils;

public class ConfigurationFactory {
	static ObjectMapper mapper;
	static {
		mapper = new ObjectMapper(new YAMLFactory());
	}
	
	public static Configuration newConfiguration(String path) {
		URI uri = ResourceUtils.getURI(path);
		return newConfiguration(uri);
		
	}
	
	
	/**
	 * create a configuration from given URI as input.
	 * Currently, content of input stream must be YML.
	 * @param uri
	 * @return
	 */
	public static Configuration newConfiguration(URI uri) {
		if (uri == null) {
			throw new IllegalArgumentException("can not create configuration from null URI");
		}
		try {
			if (uri.toURL().openStream() == null) {
				throw new IllegalArgumentException("can not create configuration from null URI");
			}
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("can not create configuration from invalid URI " + uri, ex);
		} catch (IOException ex) {
			throw new IllegalArgumentException("can not create configuration from URI " + uri, ex);
		}
		try {
			JsonNode json = mapper.readTree(uri.toURL().openStream());
			YAMLConfiguration config = new YAMLConfiguration(json);
			config.setLocation(uri);
			return config;
		} catch (Exception e) {
			throw new RuntimeException("error creating configuration", e);
		}
	}
}
