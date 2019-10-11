package com.nutanix.capacity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutanix.capacity.impl.AbstractQuantity;

public class Storage extends AbstractQuantity implements Quantity {

	public Storage(double amount, MemoryUnit unit) {
		super(ResourceKind.STORAGE, amount, unit);
	}
	public Storage(double amount, String symbol) {
		super(ResourceKind.STORAGE, amount, 
			  ResourceKind.STORAGE.getUnit(symbol));
	}

	@Override
	public Quantity clone(double amount, Unit unit) {
		return new Storage(amount, (MemoryUnit)unit);
	}

	@JsonIgnore
	@Override
	public Unit getPreferredUnit() {
		return MemoryUnit.GB;
	}
	
	@Override
	public Quantity fraction(double f) {
		return clone(getValue()*f, getUnit());
	}


}
