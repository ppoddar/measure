package com.nutanix.resource.model.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Quantity;
import com.nutanix.resource.impl.DefaultCapacity;

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
		JsonNode quantities = json.get(CapacitySerializer.QUANTITIES);
		for (JsonNode qNode : quantities) {
			Quantity q = mapper.convertValue(qNode, Quantity.class);
			cap.addQuantity(q);
		}
		return cap;
	}
	
}
