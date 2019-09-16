package com.nutanix.resource.model.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;

public class CapacitySerializer extends StdSerializer<Capacities> {

	public CapacitySerializer() {
		super(Capacities.class);
	}

	@Override
	public void serialize(Capacities capacities, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartArray();
		for (Capacity c : capacities) {
			gen.writeStartObject();
			gen.writeStringField(c.getKind().toString(), c.getAmount() + " " + c.getUnit().toString());
			gen.writeEndObject();
		}
		gen.writeEndArray();
	}

}
