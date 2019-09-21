package com.nutanix.resource.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.iterators.IteratorChain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.DefaultUtilization;
import com.nutanix.capacity.Utilization;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;

/**
 * ResourcePool is collection of {@link com.nutanix.resource.Resource}.
 * <p>
 * An external agency must {@link #addResource(Supply)
 * populate} this pool with {@link Supply supplies}.
 * <p>
 * Later, this pool can {@link #allocate(Demand) allocate}
 * resources to meet {@link Demand demand}.
 * 
 * @author pinaki.poddar
 *
 */

public class DefaultResourcePool implements ResourcePool {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultResourcePool other = (DefaultResourcePool) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String id;
	private String name;
	private List<ResourceProvider> providers
		= new ArrayList<ResourceProvider>();
	private AllocationPolicy policy = new DefaultAllocationPolicy();
	
	public DefaultResourcePool() {
		id = UUID.randomUUID().toString();
	}
	
	@Override
	public final String getId() {
		return id;
	}
	
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
		
		alloc.getSupply().reduceCapacity(alloc.getDemand());
		return alloc;
	}

	@Override
	public boolean deallocate(Allocation demand) {
		throw new AbstractMethodError();
	}

	@JsonIgnore
	@Override
	public Capacity getTotalCapacity() {
		Capacity cap = new DefaultCapacity();
		for (ResourceProvider provider : providers) {
			cap.addCapacity(provider.getTotalCapacity());
		}
		return cap.convert();
	}

	@JsonIgnore
	@Override
	public Capacity getAvailableCapacity() {
		Capacity cap = new DefaultCapacity();
		for (ResourceProvider provider : providers) {
			cap.addCapacity(provider.getAvailableCapacity());
		}
		return cap.convert();
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
	

}
