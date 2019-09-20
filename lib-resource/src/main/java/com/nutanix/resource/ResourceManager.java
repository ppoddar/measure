package com.nutanix.resource;

import java.util.Collection;
import java.util.List;

/**
 * main interface to Resource Allocation framework 
 * 
 * @author pinaki.poddar
 *
 */
public interface ResourceManager {
	public static final String POLICY_ALLOCATION   = "policy.allocation";
	public static final String CATALOG_CLUSTER_URL = "catalog.cluster.url";
	public static final String POOL_ASSIGNMENT_URL = "pool.cluster.url";

	ResourcePool getResourcePool(String id);
	ResourcePool getResourcePoolByName(String id);
	Collection<ResourcePool> getResourcePools();
	
	/**
	 * allocates given quantities.
	 * Given quantities are allocated atomically i.e.
	 * either or quantities are allocated or none.
	 * 
	 * @param pool the resource pool from where the
	 * capacities are allocated
	 * @param demand the quantities to be allocated
	 * @param options 
	 * @return an allocation 
	 */
	Allocation allocate(ResourcePool pool, 
			Capacity demand);
// TODO:			Options options);
	
	/**
	 * releases the capacity referred by given identifier.
	 * Does nothing if no allocation can be found.
	 * 
	 * @param allocId identifier to an allocation.
	 * @return true if allocated
	 */
	List<String> getPoolNames();

}
