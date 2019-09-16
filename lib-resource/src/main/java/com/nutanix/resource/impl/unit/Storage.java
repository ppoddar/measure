package com.nutanix.resource.impl.unit;

import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;

public class Storage extends AbstractCapacity implements Capacity {

	public Storage(double amount, MemoryUnit unit) {
		super(Resource.Kind.STORAGE, amount, unit);
	}

	@Override
	public Capacity clone(double amount) {
		return new Storage(amount, (MemoryUnit)this.getUnit());
	}

}
