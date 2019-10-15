package com.nutanix.resource;

import com.nutanix.capacity.Capacity;

/**
 * Policy to select a resource to meet a demand
 * 
 * @author pinaki.poddar
 *
 */
public interface AllocationPolicy {
	/**
	 * Create an allocation in terms of resource 
	 * supply being allocated to demand.
	 * <br>
	 * An allocation reduces {@link Resource#getAvailableCapacity()
	 * available capacity} of the resource.
	 * 
	 * @param pool a container of resources
	 * @param demand a set of capabilities
	 * @return null if no pool has sufficient resource
	 */
	Allocation reserveAllocation(ResourcePool pool,  
			Capacity demand,
			AllocationConstraints constaints);
	
	
}
