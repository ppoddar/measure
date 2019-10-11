package com.nutanix.resource.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.resource.prism.PrismGateway;

public class HostBuilder {
	private static Logger logger = LoggerFactory.getLogger(HostBuilder.class);
	
	public Catalog<Host> build(PrismGateway prism) throws Exception {
		Catalog<Host> hosts = new Catalog<>();
		JsonNode response = prism.getResponse("hosts/");
		for (JsonNode entity : response.get("entities")) {
			Host host = new Host(entity);
			hosts.add(host);
		}
		return hosts;
	}
}
