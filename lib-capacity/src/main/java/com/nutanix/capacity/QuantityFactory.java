package com.nutanix.capacity;

public class QuantityFactory {
	public static Quantity createQuantity(String kind, String value) {
		ResourceKind k = ResourceKind.valueOf(kind.toUpperCase());
		return k.newQuantity(value);
	}
	
	public static Quantity createQuantity(ResourceKind k, String value) {
		return k.newQuantity(value);
	}
	
	public static Quantity emptyQuantity(ResourceKind kind) {
		switch (kind) {
		case MEMORY:  return new Memory(0, MemoryUnit.MB);
		case STORAGE: return new Storage(0, MemoryUnit.GB);
		case COMPUTE: return new CPU(0);
		default:
			throw new IllegalArgumentException();
		}
	}

}
