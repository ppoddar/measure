package com.nutanix.resource.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.utils.Named;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.CapacityFactory;
import com.nutanix.capacity.Quantity;
import com.nutanix.resource.prism.PrismGateway;

public class Disk implements Named {
	private String id;
	private String storageTierName;
	private long size;
	private long capacity;
	private long used;
	private long free;
	
	public Disk(JsonNode json) {
		id = json.get("disk_uuid").asText();
		storageTierName = json.get("storage_tier_name").asText();
		size = json.get("disk_size").asLong();
		capacity = json.at("/usage_stats/storage.capacity_bytes").asLong();
		free  = json.at("/usage_stats/storage.free_bytes").asLong();
		used  = json.at("/usage_stats/storage.usage_bytes").asLong();
		
	}
	
	public String toString() {
		return "disk-" + getName() 
		    + " capacity:" + capacity 
			+ " used:" + used
			+ " free:" + free;
	}

	@Override
	public String getName() {
		return id;
	}
	
	public long getSize() {
		return size;
	}
	public long getUsed() {
		return used;
	}
	public long getAvailable() {
		return free;
	}
	public long getCapacity() {
		return capacity;
	}
	
	
}
