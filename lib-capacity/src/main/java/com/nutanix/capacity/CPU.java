package com.nutanix.capacity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.capacity.impl.AbstractQuantity;

public class CPU extends AbstractQuantity {
	public CPU(int n) {
		super(ResourceKind.COMPUTE, n, CpuUnit.NONE);
	}

	@Override
	public Quantity clone(double amount, Unit unit) {
		return new CPU((int)amount);
	}

	@JsonIgnore
	@Override
	public Unit getPreferredUnit() {
		return null;
	}

	@Override
	public Quantity fraction(double f) {
		throw new IllegalStateException("can not fraction");
	}
	


}
