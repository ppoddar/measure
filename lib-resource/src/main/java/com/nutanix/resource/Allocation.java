package com.nutanix.resource;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.nutanix.bpg.utils.Identifable;
import com.nutanix.capacity.Capacity;
import com.nutanix.resource.impl.DefaultAllocation;
/**
 * Allocation of resource demand to a resource.
 * A {@link AllocationPolicy#reserveAllocation(ResourcePool, Capacities)
 * policy} creates an allocation.
 * 
 * @author pinaki.poddar
 *
 */
@JsonTypeInfo(
		use=JsonTypeInfo.Id.CLASS, 
		include=As.PROPERTY,
		property="class")
@JsonSubTypes({
	@JsonSubTypes.Type(DefaultAllocation.class)
})

public interface Allocation 
	extends Identifable, Comparable<Allocation> {
	/**
	 * gets the resource to which demand is allocated
	 * @return a resource. never null.
	 */
	Resource getSupply();
	/**
	 * gets the demand that has been allocated
	 * @return a set of capacities. never null.
	 */
	Capacity getDemand();
	
	/**
	 * get duration to which this allocation would
	 * remain in effort
	 * @return
	 */
	Duration getDuration();
	
	/**
	 * 
	 * @param d
	 */
	void setDuration(Duration d);
	
	/**
	 * gets time when this allocation has been in effect.
	 * @return
	 */
	long getStartTime();
	
	/**
	 * gets time when this allocation is expected
	 * to have expired.
	 * @return
	 */
	long getEndTime();
	
}
