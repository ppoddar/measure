package com.nutanix.resource;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.nutanix.bpg.utils.Identifable;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Unit;
import com.nutanix.capacity.Utilization;
import com.nutanix.resource.model.Cluster;

/**
 * A resource represents capacity -- 
 * a collection of quantities 
 * (e.g. 10GB memory, 2 CPUs and 10GB storage) 
 * that are allocated/deallocated atomically.
 * 
 * <p>
 * A resource is immutable. Any mutation (e.g. adding new 
 * storage capacity) results into a new resource.
 * 
 * 
 * @author pinaki.poddar
 *
 */

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS,
	include=As.PROPERTY,
	property="class")
@JsonSubTypes({
	@JsonSubTypes.Type(Cluster.class)
})
public interface Resource extends Identifable {
	/**
	 * get capacity of all kinds.
	 * each quantity in returned capacity is expressed in
	 * {@link #getUnit(Kind) preferred unit} for the quantity.
	 * 
	 * @return a collection of capacities
	 */
	Quantity getAvailable(ResourceKind kind);
	Quantity getTotal(ResourceKind kind);
	Utilization getUtilization(ResourceKind kind);
	Capacity getAvailableCapacity();
	Capacity getTotalCapacity();
	Map<ResourceKind, Utilization> getUtilization();

	boolean acquire(Capacity q);
	boolean release(Capacity q);
	
}
