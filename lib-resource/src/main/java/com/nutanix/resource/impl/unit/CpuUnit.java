package com.nutanix.resource.impl.unit;

import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public class CpuUnit extends AbstractUnit implements Unit {

	public static final CpuUnit CPU = new CpuUnit();
	
	private CpuUnit() {
		super(Resource.Kind.COMPUTE, 1);
	}

	@Override
	public Unit getBaseUnit() {
		return null;
	}

}
