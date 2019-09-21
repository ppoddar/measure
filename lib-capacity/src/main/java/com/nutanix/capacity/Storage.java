package com.nutanix.capacity;


public class Storage extends AbstractQuantity implements Quantity {

	public Storage(double amount, MemoryUnit unit) {
		super(ResourceKind.STORAGE, amount, unit);
	}

	@Override
	public Quantity clone(double amount, Unit unit) {
		return new Storage(amount, (MemoryUnit)unit);
	}

	@Override
	public Unit getPreferredUnit() {
		return MemoryUnit.GB;
	}

}
