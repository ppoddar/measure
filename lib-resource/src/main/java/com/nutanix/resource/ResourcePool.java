package com.nutanix.resource;

import java.util.List;

import com.nutanix.bpg.measure.model.Named;
import com.nutanix.bpg.measure.utils.Identifable;

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
	/**
	 * allocates given demand to a single resource.
	 * 
	 * @param demand an allocation. never null
	 * @return if no resource available
	 */
	Allocation allocate(Capacities demand);
	boolean deallocate(Allocation demand);
	
	int getSize();
	Capacities getTotalCapacity();
	Capacities getAvailableCapacity();
	
	
	AllocationPolicy getAllocationPolicy();
	void setAllocationPolicy(AllocationPolicy policy);
	
}
