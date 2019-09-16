package com.nutanix.resource;

/**
 * provides resource to a {@link ResourcePool}.
 * 
 * @author pinaki.poddar
 *
 */
public interface ResourceProvider extends Iterable<Resource> {
	String getName();
	void setName(String name);
	Capacities getAvailableCapacities();
	Capacities getTotalCapacities();
	
	ResourceProvider addResource(Resource r);
	
	
}
