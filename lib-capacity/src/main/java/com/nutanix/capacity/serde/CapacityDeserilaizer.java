package com.nutanix.capacity.serde;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.impl.DefaultCapacity;

/**
 * deserializes {@link Capacities} as an array of
 * capacities
 * 
 * @see CapacitySerializer
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class CapacityDeserilaizer extends StdDeserializer<Capacity> {

	public CapacityDeserilaizer() {
		super(Capacity.class);
	}

	@Override
	public Capacity deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Capacity cap = new DefaultCapacity();
		ObjectMapper mapper = (ObjectMapper)p.getCodec();
		JsonNode json = mapper.readTree(p);
		Iterator<String> fieldNames = json.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			String valueString = json.get(fieldName).asText();
			ResourceKind kind = ResourceKind.valueOf(fieldName);
			Quantity q = kind.newQuantity(valueString);
			cap.addQuantity(q);
		}
		return cap;
	}
	
}
