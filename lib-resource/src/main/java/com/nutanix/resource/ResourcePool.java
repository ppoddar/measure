package com.nutanix.resource;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.nutanix.bpg.utils.Named;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Utilization;
import com.nutanix.resource.impl.DefaultResourcePool;

/**
 * a pool of resources for allocation
 * 
 * @author pinaki.poddar
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS,
		include=As.PROPERTY,
		property="class")
@JsonSubTypes({
	@JsonSubTypes.Type(DefaultResourcePool.class)
	})

public interface ResourcePool 
	extends Named, Iterable<Resource> {
	String getName();
	void setName(String name);
	void addResource(Resource resource);
	List<Resource> getResources();
	//Allocation allocate(Capacity demand);
	
	int getSize();
	Capacity getTotalCapacity();
	Capacity getAvailableCapacity();
	Map<ResourceKind, Utilization> getUtilization();

	
//	AllocationPolicy getAllocationPolicy();
//	void setAllocationPolicy(AllocationPolicy policy);
	
}
