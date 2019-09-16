package com.nutanix.resource.impl.unit;


import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;

public class Memory extends AbstractCapacity implements Capacity {

	public Memory(double amount, MemoryUnit unit) {
		super(Resource.Kind.MEMORY, amount, unit);
	}
	public Memory(double amount, String unit) {
		super(Resource.Kind.MEMORY, amount, 
				Resource.Kind.getUnit(Resource.Kind.MEMORY, unit));
	}

	@Override
	public Capacity clone(double amount) {
		return new Memory(amount, (MemoryUnit)this.getUnit());
	}

}
