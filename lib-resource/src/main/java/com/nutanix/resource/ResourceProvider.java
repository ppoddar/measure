package com.nutanix.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.nutanix.capacity.Utilization;
import com.nutanix.resource.impl.DefaultResourceProvider;
import com.nutanix.resource.model.Cluster;
import com.nutanix.capacity.Capacity;

/**
 * provides resource to a {@link ResourcePool}.
 * 
 * @author pinaki.poddar
 *
 */

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS,
include=As.PROPERTY,
property="class")
@JsonSubTypes({
	@JsonSubTypes.Type(DefaultResourceProvider.class),
	@JsonSubTypes.Type(Cluster.class)

})

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
