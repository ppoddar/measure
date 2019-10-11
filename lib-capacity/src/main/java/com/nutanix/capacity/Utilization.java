package com.nutanix.capacity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Utilization {
	@JsonIgnore
	ResourceKind getKind();
	Quantity getTotal();
	Quantity getUsed();
	Quantity getAvailable();
	double get();
	Utilization accumulate(Utilization other);
}
