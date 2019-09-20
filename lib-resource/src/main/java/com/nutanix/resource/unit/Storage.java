package com.nutanix.resource.unit;

import com.nutanix.resource.Quantity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public class Storage extends AbstractQuantity implements Quantity {

	public Storage(double amount, MemoryUnit unit) {
		super(Resource.Kind.STORAGE, amount, unit);
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
