package com.nutanix.resource.model;

import java.util.Collection;
import java.util.Collections;

public class PrimitiveType implements DataType {
	private final String name;
	public static final PrimitiveType STRING  = new PrimitiveType("String");
	public static final PrimitiveType NUMBER  = new PrimitiveType("int");
	public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean");
	
	private PrimitiveType(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return Collections.emptyList();
	}

	@Override
	public void addAttribute(DataAttribute attr) {
		throw new AbstractMethodError();
		
	}

}
