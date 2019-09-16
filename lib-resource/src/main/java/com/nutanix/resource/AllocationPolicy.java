package com.nutanix.resource;

/**
 * Policy to select a resource to meet a demand
 * 
 * @author pinaki.poddar
 *
 */
public interface AllocationPolicy {
	/**
	 * Create an allocation in terms of an assignment
	 * of a resource to a demand of capacity.
	 * <br>
	 * This assignment is tentative in a sense that
	 * available capacity of the resource is not
	 * reduced. 
	 * 
	 * @param pool a container of resources
	 * @param demand a set of capabilities
	 * @return
	 */
	Allocation reserveAllocation(
			ResourcePool pool,  
			Capacities demand);
	
	
}
