package com.nutanix.resource.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.resource.prism.PrismGateway;

public class DiskBuilder {
	private static Logger logger = LoggerFactory.getLogger(DiskBuilder.class);
	
	public Catalog<Disk> build(PrismGateway prism) throws Exception {
		
		Catalog<Disk> disks = new Catalog<>();
		
		JsonNode response = prism.getResponse("disks/");
		for (JsonNode entity : response.get("entities")) {
			Disk disk = new Disk(entity);
			disks.add(disk);
		}
		return disks;
	}
}
