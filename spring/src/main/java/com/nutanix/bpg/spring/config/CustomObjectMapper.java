package com.nutanix.bpg.measure.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.nutanix.bpg.measure.model.Catalog;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measure.spring.serde.CatalogDeserializer;
import com.nutanix.bpg.measure.spring.serde.CatalogSerializer;
import com.nutanix.bpg.measure.spring.serde.IndexibleMapDeserializer;
import com.nutanix.bpg.measure.spring.serde.IndexibleMapSerializer;
import com.nutanix.bpg.measure.utils.IndexibleMap;

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
		module.addDeserializer(Catalog.class, new CatalogDeserializer(type));
		module.addSerializer(new IndexibleMapSerializer(type2));
		module.addDeserializer(IndexibleMap.class, new IndexibleMapDeserializer(type2));

		mapper.registerModule(module);

		return mapper;
	}

}
