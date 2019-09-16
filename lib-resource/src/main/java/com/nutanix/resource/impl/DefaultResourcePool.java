package com.nutanix.resource.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.iterators.IteratorChain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;
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
	public Allocation allocate(Capacities demand) {
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

	@Override
	public Capacities getTotalCapacity() {
		Capacities cap = new DefaultCapacities();
		for (ResourceProvider provider : providers) {
			cap.addCapacities(provider.getTotalCapacities());
		}
		return cap;
	}

	@Override
	public Capacities getAvailableCapacity() {
		Capacities cap = new DefaultCapacities();
		for (ResourceProvider provider : providers) {
			cap.addCapacities(provider.getAvailableCapacities());
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

	@Override
	public int getSize() {
		int size = 0;
		for (Resource r : this) {
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
}
