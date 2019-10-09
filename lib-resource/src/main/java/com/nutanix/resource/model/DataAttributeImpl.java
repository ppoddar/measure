package com.nutanix.resource.model;

public class DataAttributeImpl implements DataAttribute {
	private final String name;
	private final DataType type;
	private final int cardinality;
	
	public DataAttributeImpl(String name, DataType type, int c) {
		this.name = name;
		this.type = type;
		this.cardinality = c;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public DataType getType() {
		return type;
	}
	@Override
	public int getCardinality() {
		return cardinality;
	}
	
	public String toString() {
		String result = type.getName();
		if (getCardinality() == -1) {
			result += "[]";
		}
		result += " " + name;
		
		return result;
	}
	
}
