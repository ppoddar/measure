package com.nutanix.bpg.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.model.MetricsDimension;
import com.nutanix.bpg.spring.serde.CatalogDeserializer;
import com.nutanix.bpg.spring.serde.CatalogSerializer;
import com.nutanix.bpg.spring.serde.IndexibleMapDeserializer;
import com.nutanix.bpg.spring.serde.IndexibleMapSerializer;
import com.nutanix.bpg.utils.IndexibleMap;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Quantity;
import com.nutanix.resource.model.serde.CapacityDeserilaizer;
import com.nutanix.resource.model.serde.CapacitySerializer;
import com.nutanix.resource.model.serde.QuantityDeserializer;
import com.nutanix.resource.model.serde.QuantitySerializer;

@Configuration
public class CustomObjectMapper {
	@Bean
	@Primary
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		CollectionLikeType type = mapper.getTypeFactory().constructCollectionLikeType(Catalog.class, Metrics.class);
		CollectionLikeType type2 = mapper.getTypeFactory().constructCollectionLikeType(IndexibleMap.class,
				MetricsDimension.class);

		module.addSerializer(new CatalogSerializer(type));
		module.addSerializer(new IndexibleMapSerializer(type2));
		module.addSerializer(new CapacitySerializer());
		module.addSerializer(new QuantitySerializer());

		module.addDeserializer(Capacity.class,     new CapacityDeserilaizer());
		module.addDeserializer(Quantity.class,     new QuantityDeserializer());
		module.addDeserializer(Catalog.class,      new CatalogDeserializer(type));
		module.addDeserializer(IndexibleMap.class, new IndexibleMapDeserializer(type2));

		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		
		mapper.registerModule(module);

		return mapper;
	}

}
