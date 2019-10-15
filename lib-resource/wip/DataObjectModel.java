package com.nutanix.resource.model;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class DataObjectModel {
	
	private Map<String, DataType> datatypes;
	private NamingPolicy namingPolicy;
	
	public DataObjectModel() {
		datatypes = new HashMap<String, DataType>();
		namingPolicy = new DefaultNamingPolicy();
	}
	
	public boolean hasType(String name) {
		return datatypes.containsKey(name);
	}
	public DataType findType(String name) {
		return datatypes.get(name);
	}
	
	public void buildType(String name, JsonNode json) {
		String javaTypeName = namingPolicy.toJavaClassName(name);
		DataType javaType = findType(javaTypeName);
		if (javaType != null && !javaType.getAttributes().isEmpty()) {
			return;
		}
		DataType dataType = newType(javaTypeName);
		
		Iterator<String> fields = json.fieldNames();
		DataAttribute attr = null;
		DataType fieldType = null;
		while (fields.hasNext()) {
			String field = fields.next();
			String javaFieldName = namingPolicy.toJavaFieldName(field);
			JsonNode node = json.get(field);
			switch (node.getNodeType()) {
			case STRING:
			case BOOLEAN:
			case NUMBER:
			case BINARY:
				fieldType = guessType(field, node);
				attr = newAttribute(javaFieldName, fieldType, 1);
				dataType.addAttribute(attr);
				break;
			case OBJECT:
				fieldType = guessType(field, node);
				attr = newAttribute(javaFieldName, fieldType, 1);
				dataType.addAttribute(attr);
				this.buildType(fieldType.getName(), node);
				break;
			case ARRAY:
				fieldType = guessType(field, node);
				attr = newAttribute(javaFieldName, fieldType, -1);
				dataType.addAttribute(attr);
				this.buildType(fieldType.getName(), ((ArrayNode)node).get(0));
				break;
			case MISSING:
			case NULL:
			case POJO:
				System.err.println("not handled " + field);
			}
		}
		
	}
	
	
	DataAttribute newAttribute(String name, DataType type, int cardinaity) {
		DataAttribute attr = new DataAttributeImpl(name, type, cardinaity);
		return attr;
	}
	
	/**
	 * given 
	 * @param name name of a json property
	 * @param node json node
	 * @return
	 */
	DataType guessType(String name, JsonNode node) {
		String javaType = namingPolicy.toJavaClassName(name);
		if (node.isValueNode()) {
			switch (node.getNodeType()) {
			case STRING:
				String value = node.asText();
				if ("string".equals(value)) {
					return PrimitiveType.STRING;
				} else {
					return newEnum(javaType, value);
				}
			case NUMBER:
				return PrimitiveType.NUMBER;
			case BOOLEAN:
				return PrimitiveType.BOOLEAN;
			default:
				throw new IllegalArgumentException();
			}
		} else if (node.isObject()) {
			if (hasType(javaType)) {
				return datatypes.get(javaType);
			} else {
				return newType(javaType);
			}
		} else if (node.isArray()) {
			String singular = namingPolicy.toSingular(name);
			javaType = namingPolicy.toJavaClassName(singular);
			if (hasType(javaType)) {
					return datatypes.get(javaType);
			} else {
					return newType(javaType);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	DataType newType(String name) {
//		name = namingPolicy.toJavaName(name);
		DataType type = new DataTypeImpl(name);
		datatypes.put(name, type);
		return type;
	}
	
	DataType newEnum(String name, String value) {
//		name = namingPolicy.toJavaName(name);
		DataType type = new EnumType(name).addOption(value);
		datatypes.put(name, type);
		return type;
	}
	
	public void print(PrintStream out) {
		for (DataType type : datatypes.values()) {
			if (EnumType.class.isInstance(type)) continue;
			out.println(type);
		}
		for (DataType type : datatypes.values()) {
			if (!EnumType.class.isInstance(type)) continue;
			out.println(type);
		}
	}
	public void print() {
		print(System.out);
	}

	
}
