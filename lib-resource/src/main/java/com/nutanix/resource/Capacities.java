package com.nutanix.resource;

import java.util.Set;

/**
 * A set of capacities.
 * 
 * @author pinaki.poddar
 *
 */
public interface Capacities extends Iterable<Capacity> {
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
	Capacity getCapacity(Resource.Kind kind);
	
	/**
	 * adds given {@link Capacity}.
	 * 
	 * @param cap a capacity
	 * @throws IllegalArgumentException if capacity is null
	 */
	void addCapacity(Capacity cap);
	void addCapacities(Capacities cap);
	void reduceCapacity(Capacity cap);
	void reduceCapacities(Capacities cap);
}
