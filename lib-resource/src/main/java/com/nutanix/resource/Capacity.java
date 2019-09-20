package com.nutanix.resource;

import java.util.Set;

/**
 * A set of capacities.
 * 
 * @author pinaki.poddar
 *
 */
public interface Capacity extends Iterable<Quantity> {
	/**
	 * affirms if this container has any capacity 
	 * of given kind
	 * @param kind
	 * @return
	 */
	boolean hasKind(Resource.Kind kind);
	
	/**
	 * gets kind of capacities held by this receiver
	 * @return
	 */
	Set<Resource.Kind> getKinds();
	
	/**
	 * gets capacity of given kind
	 * @param kind a kind of capacity
	 * @return
	 * @throws IllegalArgumentException if {@link #hasKind(com.nutanix.resource.Resource.Kind)}
	 * has returned false
	 */
	Quantity getQuantity(Resource.Kind kind);
	
	/**
	 * adds given {@link Capacity}.
	 * 
	 * @param cap a capacity
	 * @throws IllegalArgumentException if capacity is null
	 */
	void addQuantity(Quantity cap);
	void addCapacities(Capacity cap);
	void reduceQuantity(Quantity cap);
	void reduceCapacity(Capacity cap);
	
	Capacity convert();
}
