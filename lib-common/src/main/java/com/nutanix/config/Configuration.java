package com.nutanix.config;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * configuration is similar to {@link java.util.Properties
 * properties}. 
 * @author pinaki.poddar
 *
 */
public interface Configuration {
	
	JsonNode asJson();
	Map<String, String> asMap();
	String getString(String key);
	String getOptionalString(String key);
	String getString(String key, String def);
	/**
	 * gets a configuration of a nested section.
	 * If given key refers to a string, the string
	 * is interpreted as location to another 
	 * configuration relative to location of this
	 * configuration.
	 * 
	 * @param key
	 * @return
	 */
	Configuration getSection(String key);
	
	/**
	 * location of this configuration. Used to resolve
	 * and relative reference.
	 * @return
	 */
	URI getLocation();
	Path getPath();
	
	Path resolvePath(String key);
}
