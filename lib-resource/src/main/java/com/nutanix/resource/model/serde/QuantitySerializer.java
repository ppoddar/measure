package com.nutanix.resource.model.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nutanix.resource.Quantity;

@SuppressWarnings("serial")
public class QuantitySerializer extends StdSerializer<Quantity> {

	public QuantitySerializer() {
		super(Quantity.class);
	}
	@Override
	public void serialize(Quantity q, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		String value = q.getValue() + " " + q.getUnit();
		gen.writeStringField(q.getKind().toString(), value);
		gen.writeEndObject();
	}
}
