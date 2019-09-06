package com.nutanix.bpg.workload;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

public class StringToIntArrayConverter implements 
	Converter<String, int[]> {

	@Override
	public int[] convert(String value) {
		String[] items = value.split("\\s+");
		int[] result = new int[items.length];
		int i = 0;
		for (String e : items) {
			result[i++] = Integer.parseInt(e);
		}
		return result;
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		return new ObjectMapper().constructType(String.class);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		return new ObjectMapper().constructType(int[].class);
	}

}
