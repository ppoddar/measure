package com.nutanix.resource.model;

public interface NamingPolicy {
	String toJavaClassName(String jsonName);
	String toJavaFieldName(String jsonName);
	String toJsonName(String javaName);
	String toSingular(String name);
}
