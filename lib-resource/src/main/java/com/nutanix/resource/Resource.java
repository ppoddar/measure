package com.nutanix.resource;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.nutanix.bpg.utils.Identifable;
import com.nutanix.capacity.Utilization;
import com.nutanix.resource.model.VirtualMachine;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Unit;

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
	@JsonSubTypes.Type(VirtualMachine.class)
})
public interface Resource extends Identifable {
		
	/**
	 * get capacity of all kinds.
	 * each quantity in returned capacity is expressed in
	 * {@link #getUnit(Kind) preferred unit} for the quantity.
	 * 
	 * @return a collection of capacities
	 */
	Capacity getAvailableCapacity();
	Capacity getTotalCapacity();
	
	/**
	 * get capacity of given kind.
	 * @return capacity of given kind, if exists
	 * @throws IllegalArgumentException if capcity
	 * of given kind does not exist
	 */
	
	/**
	 * affirms if any capacity of given kind exists
	 * 
	 * @param kind a kind e.g. memory, cpu, storage etc.
	 * not case-sensitive
	 * @return
	 */
	boolean hasKind(ResourceKind kind);
	
	/**
	 * get kinds of capacities provided by this receiver
	 * 
	 * @return a set of {@link Resource.Kind kinds}
	 * {@link Resource.Kind#MEMORY memory}.
	 */
	@JsonIgnore
	Collection<ResourceKind> getKinds();
	
	/**
	 * the unit in which given kind of resource capacity
	 * is expressed.
	 * @param kind
	 * @return an unit.
	 */
	Unit getUnit(ResourceKind kind);
	
	/**
	 * add capacity to this resource.
	 * @param q a quantity.
	 * @return same receiver
	 */
	Resource addQuanity(Quantity q);
	
	/**
	 * reduce capacity from this resource.
	 * @param q
	 * @return same receiver
	 */
	Resource reduceCapacity(Capacity q);

	/**
	 * gets utilization of this receiver.
	 * 
	 * @return utilization
	 */
	Utilization getUtilization();
	
}
