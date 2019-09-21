package com.nutanix.capacity;


public class CpuUnit extends AbstractUnit implements Unit {

	public static final CpuUnit NONE = new CpuUnit();
	
	private CpuUnit() {
		super(ResourceKind.COMPUTE, 1, "");
	}

	@Override
	public Unit getBaseUnit() {
		return null;
	}


}
