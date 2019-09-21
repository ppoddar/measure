package com.nutanix.capacity;


public class CPU extends AbstractQuantity {
	public CPU(int n) {
		super(ResourceKind.COMPUTE, n, CpuUnit.NONE);
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
