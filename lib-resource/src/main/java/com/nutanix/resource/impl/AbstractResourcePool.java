package com.nutanix.resource.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.iterators.IteratorChain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Utilization;
import com.nutanix.capacity.impl.DefaultUtilization;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;

public abstract class AbstractResourcePool implements ResourcePool {
	private String name;
	private List<ResourceProvider> providers
		= new ArrayList<ResourceProvider>();
	private AllocationPolicy policy = 
			new DefaultAllocationPolicy();
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addProvider(ResourceProvider supplier) {
		providers.add(supplier);
	}

	@Override
	public Allocation allocate(Capacity demand) {
		assertProviders();
		Allocation alloc = getAllocationPolicy()
					.reserveAllocation(this, demand);
		alloc.getSupply().acquire(demand);
		return alloc;
	}

	@Override
	public boolean deallocate(Allocation demand) {
		throw new AbstractMethodError();
	}

	@Override
	public Capacity getTotalCapacity() {
		Capacity cap = newCapacity();
		for (ResourceProvider provider : providers) {
			cap.addCapacity(provider.getTotalCapacity());
		}
		return cap;
	}

	@Override
	public Capacity getAvailableCapacity() {
		Capacity cap = newCapacity();
		for (ResourceProvider provider : providers) {
			cap.addCapacity(provider.getAvailableCapacity());
		}
		return cap;
	}
	
	@JsonIgnore
	@Override
	public AllocationPolicy getAllocationPolicy() {
		return policy;
	}

	@Override
	public void setAllocationPolicy(AllocationPolicy policy) {
		this.policy = policy;
	}

	/**
	 * a pool iterates over all resources of all
	 * resource provides.
	 * <p>
	 *  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Resource> iterator() {
		IteratorChain iterator = new IteratorChain();
		for (ResourceProvider provider : providers) {
			iterator.addIterator(provider.iterator());
		}
		return (Iterator<Resource>)iterator;
	}
	
	void assertProviders() {
		if (providers.isEmpty()) {
			throw new IllegalStateException("no providers in pool " + this);
		}
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
	public List<String> getProviderNames() {
		List<String> names = new ArrayList<>();
		providers.forEach((p)->{names.add(p.getName());});
		return names;
	}

	@Override
	public List<ResourceProvider> getProviders() {
		return providers;
	}

	@Override
	public Utilization getUtilization() {
		Utilization result = new DefaultUtilization();
		for (ResourceProvider provider : providers) {
			result = result.accumulate(provider.getUtilization());
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
		AbstractResourcePool other = AbstractResourcePool.class.cast(obj);
		
		return other.getName().equals(this.getName());
	}
	
	protected abstract Capacity newCapacity();


}
