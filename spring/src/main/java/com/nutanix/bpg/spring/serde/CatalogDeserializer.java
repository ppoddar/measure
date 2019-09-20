package com.nutanix.bpg.spring.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.model.Metrics;

@SuppressWarnings("serial")
public class CatalogDeserializer extends 
	StdDeserializer<Catalog<Metrics>> {

	public CatalogDeserializer(JavaType vc) {
		super(vc);
	}

	@Override
	public Catalog<Metrics> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		Catalog<Metrics> catalog = new Catalog<Metrics>();
		catalog.setName(node.get("name").asText());
		ObjectMapper mapper = (ObjectMapper)p.getCodec();
		JsonNode array = node.get("elements");
		for (JsonNode e : array) {
			Metrics m = mapper.convertValue(e, Metrics.class);
			catalog.add(m);
		}
		return catalog;
	}
}
