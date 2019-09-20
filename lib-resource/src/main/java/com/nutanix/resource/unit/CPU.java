package com.nutanix.resource.unit;

import com.nutanix.resource.Quantity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public class CPU extends AbstractQuantity {
	public CPU(int n) {
		super(Resource.Kind.COMPUTE, n, CpuUnit.NONE);
	}

	@Override
	public Quantity clone(double amount, Unit unit) {
		return new CPU((int)amount);
	}

	@Override
	public Unit getPreferredUnit() {
		return null;
	}

}
