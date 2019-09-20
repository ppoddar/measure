package com.nutanix.resource.unit;


import com.nutanix.resource.Quantity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public class Memory extends AbstractQuantity implements Quantity {

	public Memory(double amount, MemoryUnit unit) {
		super(Resource.Kind.MEMORY, amount, unit);
	}
	public Memory(double amount, String unit) {
		super(Resource.Kind.MEMORY, amount, 
				Resource.Kind.getUnit(Resource.Kind.MEMORY, unit));
	}

	@Override
	public Quantity clone(double amount, Unit unit) {
		return new Memory(amount, (MemoryUnit)unit);
	}
	@Override
	public Unit getPreferredUnit() {
		return MemoryUnit.MB;
	}

}
