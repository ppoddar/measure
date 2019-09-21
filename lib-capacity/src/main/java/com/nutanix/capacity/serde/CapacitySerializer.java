package com.nutanix.capacity.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Quantity;
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
	//public static String QUANTITIES = "quantities";
	public CapacitySerializer() {
		super(Capacity.class);
	}

	@Override
	public void serialize(Capacity capacity, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		for (Quantity q : capacity) {
			String fieldName = q.getKind().toString();
			String valueString = String.format("%s %s", q.getValue(), q.getUnit());
			gen.writeStringField(fieldName, valueString);
		}
		gen.writeEndObject();
	}

}
