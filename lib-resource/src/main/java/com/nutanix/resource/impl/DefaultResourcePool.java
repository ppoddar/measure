package com.nutanix.resource.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Utilization;
import com.nutanix.capacity.impl.DefaultCapacity;
import com.nutanix.capacity.impl.DefaultUtilization;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourcePool;

public class DefaultResourcePool implements ResourcePool {
	private String name;
	private List<Resource> resources;
	private AllocationPolicy policy; 
	
	
	protected DefaultResourcePool() {
		resources = new ArrayList<Resource>();
		policy = new DefaultAllocationPolicy();
	}
	
	Capacity newCapacity() {
		Capacity cap = new DefaultCapacity();
		cap.setPreferredUnit(ResourceKind.MEMORY, MemoryUnit.GB);
		cap.setPreferredUnit(ResourceKind.STORAGE, MemoryUnit.TB);
		return cap;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addResource(Resource r) {
		resources.add(r);
	}

//	@Override
//	public Allocation allocate(Capacity demand) {
//		Allocation alloc = getAllocationPolicy()
//					.reserveAllocation(this, demand);
//		alloc.getSupply().acquire(demand);
//		return alloc;
//	}

	

//	@JsonIgnore
//	@Override
//	public AllocationPolicy getAllocationPolicy() {
//		return policy;
//	}
//
//	@Override
//	public void setAllocationPolicy(AllocationPolicy policy) {
//		this.policy = policy;
//	}

	/**
	 * a pool iterates over all resources 
	 * <p>
	 *  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Resource> iterator() {
		return resources.iterator();
	}
	
	

	@JsonIgnore
	@Override
	public int getSize() {
		int size = 0;
		for (@SuppressWarnings("unused") Resource r : this) {
			size++;
		}
		return size;
	}


	@Override
	public List<Resource> getResources() {
		return resources;
	}

	@Override
	public Map<ResourceKind, Utilization> getUtilization() {
		Map<ResourceKind, Utilization> result 
			= new HashMap<ResourceKind, Utilization>();
		for (Resource r : resources) {
			Map<ResourceKind, Utilization> u = r.getUtilization();
			for (ResourceKind kind : ResourceKind.values()) {
				Utilization u1 = u.get(kind);
				Utilization u2 = r.getUtilization(kind);
				if (u1 != null) {
					result.put(kind, u1.accumulate(u2));
				} else if (u2 != null) {
					result.put(kind, u2);
				}
			}
		}
		return result;
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!getClass().isInstance(obj))
			return false;
		DefaultResourcePool other = DefaultResourcePool.class.cast(obj);
		
		return other.getName().equals(this.getName());
	}

	@Override
	public Capacity getTotalCapacity() {
		Capacity total = newCapacity();
		for (Resource r : this) {
			total.addCapacity(r.getTotalCapacity());
		}
		return total;
	}
	
	@Override
	public Capacity getAvailableCapacity() {
		Capacity available = newCapacity();
		for (Resource r : this) {
			available.addCapacity(r.getAvailableCapacity());
		}
		return available;
	}
	

}
