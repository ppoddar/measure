package com.nutanix.resource;

import java.util.List;

import com.nutanix.bpg.model.Named;
import com.nutanix.bpg.utils.Identifable;

/**
 * a pool of resources for allocation
 * 
 * @author pinaki.poddar
 *
 */


public interface ResourcePool 
	extends Identifable, Named, Iterable<Resource> {
	String getId();
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
