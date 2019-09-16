package com.nutanix.resource.model.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;
import com.nutanix.resource.impl.DefaultCapacities;
import com.nutanix.resource.impl.unit.CPU;
import com.nutanix.resource.impl.unit.Memory;
import com.nutanix.resource.impl.unit.MemoryUnit;


@SuppressWarnings("serial")
public class CapacityDeserilaizer extends StdDeserializer<Capacities> {

	public CapacityDeserilaizer() {
		super(Capacities.class);
	}

	@Override
	public Capacities deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Capacities cap = new DefaultCapacities();
		assertToken(p.currentToken(), JsonToken.START_ARRAY);
		p.nextToken();
		while (true)  {
			Capacity c = readCapacity(p);
			cap.addCapacity(c);
			if (p.nextToken() != JsonToken.START_OBJECT) {
				break;
			}
		} 
		return cap;
	}
	
	Capacity readCapacity(JsonParser p) throws IOException {
		Capacity cap = null;
		String field = p.nextFieldName();
		String value = p.nextTextValue();		
		Resource.Kind kind = Resource.Kind.valueOf(field);
		String[] tokens = value.split(" ");
		double amount = Double.parseDouble(tokens[0]);
		switch (kind) {
		case MEMORY:
		case STORAGE:
			Unit unit = Resource.Kind.getUnit(kind, tokens[1]);
			cap = new Memory(amount, (MemoryUnit)unit);
			break;
		case COMPUTE:
			cap = new CPU((int)amount);
			break;
		default:
			throw new IllegalArgumentException();
		}
		p.nextToken();
		return cap;
	}
	
	void assertToken(JsonToken token, JsonToken expected) throws IOException {
		if (token != expected) {
			throw new IllegalStateException("error JSON parsing"
					+ " expected " + expected
					+ " found " + token);
		}
	}

}
