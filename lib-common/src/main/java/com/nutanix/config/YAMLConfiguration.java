package com.nutanix.config;

import com.fasterxml.jackson.databind.JsonNode;

public class YAMLConfiguration extends AbstractConfiguration {

	public YAMLConfiguration(JsonNode data) {
		super.init(data);
	}
	
	@Override
	protected AbstractConfiguration newInstance(JsonNode data) {
		YAMLConfiguration config = new YAMLConfiguration(data); 
		return config;
	}


}
