package com.nutanix.resource;

import java.util.Collection;

import com.nutanix.bpg.measure.utils.Identifable;
import com.nutanix.resource.impl.unit.MemoryUnit;

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
public interface Resource extends Identifable {
	
	public static enum Kind {
		COMPUTE, 
		MEMORY, 
		STORAGE;
		
		public static Unit getUnit(Kind kind, String symbol) {
			switch (symbol) {
			case "B": return MemoryUnit.B; 
			case "KB": return MemoryUnit.KB; 
			case "MB": return MemoryUnit.MB; 
			case "GB": return MemoryUnit.GB; 
			default:
				return null;
			}
			
		}
	}
		
	/**
	 * get capacity of all kinds
	 * @return a collection of capacities
	 */
	Capacities getCapacities();
	Capacities getMaxCapacities();
	
	/**
	 * get capacity of given kind.
	 * @return capacity of given kind, if exists
	 * @throws IllegalArgumentException if capcity
	 * of given kind does not exist
	 */
	Capacity getCapacity(Resource.Kind kind);
	
	/**
	 * affirms if any capacity of given kind exists
	 * 
	 * @param kind a kind e.g. memory, cpu, storage etc.
	 * not case-sensitive
	 * @return
	 */
	boolean hasKind(Resource.Kind kind);
	
	/**
	 * get kinds of capacities of this receiver
	 * @return
	 */
	Collection<Resource.Kind> getKinds();
	
	/**
	 * add capacity to this resource.
	 * @param q
	 * @return same receiver
	 */
	Resource addCapacity(Capacity q);
	
	/**
	 * reduce capacity from this resource.
	 * @param q
	 * @return same receiver
	 */
	Resource reduceCapacity(Capacities q);

}
