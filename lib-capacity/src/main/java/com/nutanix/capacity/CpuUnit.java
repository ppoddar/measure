package com.nutanix.capacity;

import com.nutanix.capacity.impl.AbstractUnit;

public class CpuUnit extends AbstractUnit implements Unit {

	public static final CpuUnit NONE = new CpuUnit();
	
	private CpuUnit() {
		super(ResourceKind.COMPUTE, 1, "");
	}

	


}
