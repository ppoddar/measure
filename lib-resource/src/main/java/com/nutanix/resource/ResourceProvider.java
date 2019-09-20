package com.nutanix.resource;

/**
 * provides resource to a {@link ResourcePool}.
 * 
 * @author pinaki.poddar
 *
 */
public interface ResourceProvider extends Iterable<Resource> {
	String getId();
	String getName();
	void setName(String name);
	Capacity getAvailableCapacity();
	Capacity getTotalCapacity();
	Utilization getUtilization();
	
	ResourceProvider addResource(Resource r);
	
	int getResourceCount();
	
	
}
