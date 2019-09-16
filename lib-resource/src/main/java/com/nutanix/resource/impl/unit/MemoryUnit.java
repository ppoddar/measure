package com.nutanix.resource.impl.unit;

import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public class MemoryUnit extends AbstractUnit implements Unit {

	public static MemoryUnit GB = new MemoryUnit(1024*1024, "GB");
	public static MemoryUnit MB = new MemoryUnit(1024, "MB");
	public static MemoryUnit KB = new MemoryUnit(1, "KB");;
	public static MemoryUnit B  = new MemoryUnit(1.0/1024.0, "B");;
	
	private MemoryUnit(double f, String symbol) {
		super(Resource.Kind.MEMORY, f, symbol);
	}

	@Override
	public Unit getBaseUnit() {
		return KB;
	}

}
