package com.nutanix.bpg.spring.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.model.Metrics;

@SuppressWarnings("serial")
public class CatalogSerializer extends StdSerializer<Catalog<Metrics>> {
	public CatalogSerializer(JavaType t) {
		super(t);
	}

	@Override
	public void serialize(Catalog<Metrics> catalog, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("name", catalog.getName());
		gen.writeArrayFieldStart("elements");
		for (Object e : catalog) {
			gen.writeObject(e);
		}
		gen.writeEndArray();
		gen.writeEndObject();
	}
}
