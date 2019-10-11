package com.nutanix.resource.impl;

import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.impl.DefaultCapacity;

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
