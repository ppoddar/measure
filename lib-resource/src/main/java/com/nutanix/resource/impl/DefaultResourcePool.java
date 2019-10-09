package com.nutanix.resource.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.iterators.IteratorChain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Utilization;
import com.nutanix.capacity.impl.DefaultCapacity;
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

public class DefaultResourcePool extends AbstractResourcePool {

	@Override
	protected Capacity newCapacity() {
		Capacity cap = new DefaultCapacity();
		cap.setPreferredUnit(ResourceKind.MEMORY,  MemoryUnit.GB);
		cap.setPreferredUnit(ResourceKind.STORAGE, MemoryUnit.TB);
		return cap;
	}

}
