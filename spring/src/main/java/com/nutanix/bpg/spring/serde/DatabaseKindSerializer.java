package com.nutanix.bpg.spring.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nutanix.bpg.model.DatabaseKind;

@SuppressWarnings("serial")
public class DatabaseKindSerializer extends StdSerializer<DatabaseKind>{

	protected DatabaseKindSerializer() {
		super(DatabaseKind.class);
	}

	@Override
	public void serialize(DatabaseKind value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("kind", value.getName());
		gen.writeEndObject();
	}

}
