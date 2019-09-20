package com.nutanix.bpg.spring.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * reads application.{profile}.yml file
 *  
 *  
 * @author pinaki.poddar
 *
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class YAMLConfig {
	private String name;
	
	private Map<String, String> catalog
		= new HashMap<String, String>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getCatalog() {
		return catalog;
	}

	public void setCatalog(Map<String, String> catalog) {
		this.catalog = catalog;
	}
}
