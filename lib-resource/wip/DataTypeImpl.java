package com.nutanix.resource.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataTypeImpl implements DataType {
	private final String name;
	private  Map<String, DataAttribute> attrs;
	
	public DataTypeImpl(String name) {
		this.name = name;
		attrs = new HashMap<String, DataAttribute>();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return attrs.values();
	}

	@Override
	public void addAttribute(DataAttribute attr) {
		attrs.put(attr.getName(), attr);
	}
	
	public String toString() {
		String result = "Type: " + getName();
		for (DataAttribute attr : attrs.values()) {
			result += "\r\n\t" + attr;
		}
		return result;
	}

}
