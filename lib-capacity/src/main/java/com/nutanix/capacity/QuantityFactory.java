package com.nutanix.capacity;

public class QuantityFactory {
	public static Quantity createQuantity(String kind, String value) {
		ResourceKind k = ResourceKind.valueOf(kind.toUpperCase());
		return k.newQuantity(value);
	}
}
