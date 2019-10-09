package com.nutanix.capacity;

import java.util.Map;

import com.nutanix.capacity.impl.DefaultCapacity;

public class CapacityFactory {
	public static Capacity newCapacity(Map<String, String> quantities) {
		Capacity cap = new DefaultCapacity();
		for (Map.Entry<String, String> e : quantities.entrySet()) {
			Quantity q = QuantityFactory.createQuantity(e.getKey(), e.getValue());
			cap.addQuantity(q);
		}
		return cap;
	}
}
