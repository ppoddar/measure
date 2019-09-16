package com.nutanix.resource.impl.unit;

import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;

public class CPU extends AbstractCapacity {
	public CPU(int n) {
		super(Resource.Kind.COMPUTE, n, CpuUnit.CPU);
	}

	@Override
	public Capacity clone(double amount) {
		return new CPU((int)amount);
	}

}
