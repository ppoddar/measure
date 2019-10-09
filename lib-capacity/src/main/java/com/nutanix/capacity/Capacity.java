package com.nutanix.capacity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.nutanix.capacity.impl.DefaultCapacity;


/**
 * A set of capacities.
 * 
 * @author pinaki.poddar
 *
 */


@JsonSubTypes({
	@JsonSubTypes.Type(DefaultCapacity.class)
})
public interface Capacity extends Iterable<Quantity> {
	/**
	 * affirms if this container has any capacity 
	 * of given kind
	 * @param kind
	 * @return
	 */
	//boolean hasKind(ResourceKind kind);
	
	/**
	 * gets kind of capacities held by this receiver
	 * @return
	 */
	//Set<ResourceKind> getKinds();
	
	/**
	 * gets capacity of given kind
	 * @param kind a kind of capacity
	 * @return
	 * @throws IllegalArgumentException if {@link #hasKind(com.nutanix.resource.Resource.Kind)}
	 * has returned false
	 */
	Quantity getQuantity(ResourceKind kind);
	
	/**
	 * adds given {@link Capacity}.
	 * 
	 * @param cap a capacity
	 * @throws IllegalArgumentException if capacity is null
	 */
	void addQuantity(Quantity cap);
	void addCapacity(Capacity cap);
	void reduceQuantity(Quantity cap);
	void reduceCapacity(Capacity cap);
	
	/**
	 * sets the unit for given resource kind. 
	 * @param kind
	 * @param unit
	 */
	void setPreferredUnit(ResourceKind kind, Unit unit);
	/**
	 * gets the unit for given resource kind. 
	 * @param kind
	 */
	Unit getPreferredUnit(ResourceKind kind);
}
