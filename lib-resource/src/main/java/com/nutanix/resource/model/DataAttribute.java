package com.nutanix.resource.model;

public interface DataAttribute {
	String getName();
	DataType getType();
	int getCardinality();
}
