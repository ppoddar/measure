package com.nutanix.resource.model.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Quantity;
/**
 * Serializes capacities as follows:
 * <pre>
 *   {
 *    "MEMORY": "1234 MB",
 *    "COMPUTE": 4,
 *    "STORAGE": "50 GB"
 *   }
 * </pre>
 * 
 * @see CapacityDeserilaizer
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class CapacitySerializer extends StdSerializer<Capacity> {
	public static String QUANTITIES = "quantities";
	public CapacitySerializer() {
		super(Capacity.class);
	}

	@Override
	public void serialize(Capacity capacities, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeArrayFieldStart(QUANTITIES);
		for (Quantity q : capacities) {
			gen.writeObject(q);
		}
		gen.writeEndArray();
		gen.writeEndObject();
	}

}
