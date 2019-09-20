package com.nutanix.resource;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.bpg.utils.Identifable;
import com.nutanix.resource.unit.CpuUnit;
import com.nutanix.resource.unit.MemoryUnit;

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
		
		String[] unitSymbols;
		
		public Unit getBaseUnit() {
			switch (this) {
			case MEMORY: 
			case STORAGE: return MemoryUnit.KB;
			case COMPUTE: 
				return CpuUnit.NONE;
			default: return null;
			}
		}
		public Unit getHighestUnit() {
			switch (this) {
			case MEMORY: 
			case STORAGE: return MemoryUnit.GB;
			case COMPUTE: 
			default: return null;
			}
		}
		public Unit getSmallestUnit() {
			switch (this) {
			case MEMORY: 
			case STORAGE: return MemoryUnit.B;
			case COMPUTE: 
			default: return null;
			}
		}
		
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
	boolean hasKind(Resource.Kind kind);
	
	/**
	 * get kinds of capacities provided by this receiver
	 * 
	 * @return a set of {@link Resource.Kind kinds}
	 * {@link Resource.Kind#MEMORY memory}.
	 */
	@JsonIgnore
	Collection<Resource.Kind> getKinds();
	
	/**
	 * the unit in which given kind of resource capcity
	 * is expressed.
	 * @param kind
	 * @return an unit.
	 */
	Unit getUnit(Resource.Kind kind);
	
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
