package com.nutanix.resource.model;

import java.util.Collection;

public interface DataType {
	String getName();
	Collection<DataAttribute> getAttributes();
	void addAttribute(DataAttribute attr);
}
