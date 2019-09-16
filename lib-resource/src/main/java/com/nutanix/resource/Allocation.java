package com.nutanix.resource;

import java.time.Duration;
import java.util.Map;

import com.nutanix.bpg.measure.utils.Identifable;
/**
 * Allocation of resource demand to a resource.
 * A {@link AllocationPolicy#reserveAllocation(ResourcePool, Capacities)
 * policy} creates an allocation.
 * 
 * @author pinaki.poddar
 *
 */
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
	Capacities getDemand();
	
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
