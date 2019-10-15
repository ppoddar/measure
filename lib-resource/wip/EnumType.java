package com.nutanix.resource.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EnumType implements DataType {
	private final String name;
	private List<String> options;
	public EnumType(String name) {
		this.name = name;
		this.options = new ArrayList<String>();
	}
	
	public EnumType addOption(String opt) {
		options.add(opt);
		return this;
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
	
	public String toString() {
		String result = "Enum: " + getName();
		result += " " + options.toString();
		
		return result;
	}

}
