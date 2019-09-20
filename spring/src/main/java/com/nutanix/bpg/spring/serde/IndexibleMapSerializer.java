package com.nutanix.bpg.spring.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nutanix.bpg.model.MetricsDimension;
import com.nutanix.bpg.utils.IndexibleMap;

@SuppressWarnings("serial")
public class IndexibleMapSerializer extends StdSerializer<IndexibleMap<MetricsDimension>> {

	
	public IndexibleMapSerializer(JavaType t) {
		super(t);
	}

	@Override
	public void serialize(IndexibleMap<MetricsDimension> catalog, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartArray();
		for (Object e : catalog) {
			gen.writeObject(e);
		}
		gen.writeEndArray();
		gen.writeEndObject();
	}
	

}
