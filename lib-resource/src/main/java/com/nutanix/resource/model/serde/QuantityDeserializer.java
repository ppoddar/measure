package com.nutanix.resource.model.serde;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nutanix.resource.Quantity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;
import com.nutanix.resource.unit.CPU;
import com.nutanix.resource.unit.Memory;
import com.nutanix.resource.unit.MemoryUnit;
import com.nutanix.resource.unit.Storage;

@SuppressWarnings("serial")
public class QuantityDeserializer extends StdDeserializer<Quantity> {

	public QuantityDeserializer() {
		super(Quantity.class);
	}

	@Override
	public Quantity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode json = p.getCodec().readTree(p);
		Iterator<String> name = json.fieldNames();
		String kindString = name.next();
		if (name.hasNext()) {
			throw new InternalError("error deserializing quantity"
					+ " expected a single field but found more");
		}
		String valueString = json.get(kindString).asText();
		Resource.Kind kind = Resource.Kind.valueOf(kindString.toUpperCase());
		Quantity q = null;
		
		String[] tokens = valueString.split(" ");
		Double amount = Double.parseDouble(tokens[0]);
		Unit unit = Resource.Kind.getUnit(kind, 
				((tokens.length > 1) ? tokens[1] : ""));
		switch (kind) {
		case MEMORY:
			q = new Memory(amount,  (MemoryUnit)unit);
			break;
		case STORAGE:
			q = new Storage(amount, (MemoryUnit)unit);
			break;
		case COMPUTE:
			q = new CPU(amount.intValue());
			break;
		default:
		}
		return q;
	}

}
