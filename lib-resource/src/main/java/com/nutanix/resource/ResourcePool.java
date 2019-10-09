package com.nutanix.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.nutanix.bpg.utils.Identifable;
import com.nutanix.bpg.utils.Named;
import com.nutanix.capacity.Utilization;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.capacity.Capacity;

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
	void addProvider(ResourceProvider supplier);
	
	List<String> getProviderNames();
	List<ResourceProvider> getProviders();
	/**
	 * allocates given demand to a single resource.
	 * 
	 * @param demand an allocation. never null
	 * @return if no resource available
	 */
	Allocation allocate(Capacity demand);
	boolean deallocate(Allocation demand);
	
	int getSize();
	Capacity getTotalCapacity();
	Capacity getAvailableCapacity();
	Utilization getUtilization();

	
	AllocationPolicy getAllocationPolicy();
	void setAllocationPolicy(AllocationPolicy policy);
	
}
