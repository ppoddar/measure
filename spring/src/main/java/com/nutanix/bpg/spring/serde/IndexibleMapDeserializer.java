package com.nutanix.bpg.spring.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nutanix.bpg.model.MetricsDimension;
import com.nutanix.bpg.utils.IndexibleMap;

@SuppressWarnings("serial")
public class IndexibleMapDeserializer extends 
	StdDeserializer<IndexibleMap<MetricsDimension>> {

	public IndexibleMapDeserializer(JavaType vc) {
		super(vc);
	}

	@Override
	public IndexibleMap<MetricsDimension> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode array = p.getCodec().readTree(p);
		IndexibleMap<MetricsDimension> map = new IndexibleMap<MetricsDimension>();
		ObjectMapper mapper = (ObjectMapper)p.getCodec();
		for (JsonNode e : array) {
			MetricsDimension m = mapper.convertValue(e, MetricsDimension.class);
			map.put(m);
		}
		return map;
	}
	

}
