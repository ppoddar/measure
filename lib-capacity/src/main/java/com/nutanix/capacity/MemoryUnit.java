package com.nutanix.capacity;

import com.nutanix.capacity.impl.AbstractUnit;

public class MemoryUnit extends AbstractUnit implements Unit {

	public static MemoryUnit TB = new MemoryUnit(1024*1024*1024, "TB");
	public static MemoryUnit GB = new MemoryUnit(1024*1024, "GB");
	public static MemoryUnit MB = new MemoryUnit(1024, "MB");
	public static MemoryUnit KB = new MemoryUnit(1, "KB");;
	public static MemoryUnit B  = new MemoryUnit(1.0/1024.0, "B");;
	
	public static MemoryUnit fromString(String symbol) {
		switch (symbol.toUpperCase()) {
		case "TB": return TB;
		case "GB": return GB;
		case "MB": return MB;
		case "KB": return KB;
		case "B": return B;
		default:
			throw new IllegalArgumentException("unkown Memory unit sysmble " + symbol);
		}
	}
	
	private MemoryUnit(double f, String symbol) {
		super(ResourceKind.MEMORY, f, symbol);
	}

}
