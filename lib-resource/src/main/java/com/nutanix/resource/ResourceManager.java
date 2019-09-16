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
	public static final String POLICY_ALLOCATION = "policy.allocation";
	public static final String CATALOG_CLUSTER_URL = "catalog.cluster.url";

	ResourcePool getResourcePool(String id);
	List<ResourcePool> getResourcePools();
	
	Allocation allocate(ResourcePool pool, Collection<Capacity> demand);
	boolean deallocate(String allocId);
	Allocation findAllocation(String allocId);
	
}
