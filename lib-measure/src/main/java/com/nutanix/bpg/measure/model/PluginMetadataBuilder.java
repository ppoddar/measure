package com.nutanix.bpg.measure.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutanix.bpg.measure.utils.JsonUtils;
import com.nutanix.bpg.measure.utils.ObjectFactory;

/**
 * Creates a {@link PluginMetadata} from *.yaml.
 * 
 * @author pinaki.poddar
 *
 */
public class PluginMetadataBuilder implements Factory<PluginMetadata> {
	public static final String PROPERTY_CLASS       = "class";
	public static final String PROPERTY_NAME        = "name";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_METRICS     = "metrics";
	
	private static final Logger logger = LoggerFactory.getLogger(PluginMetadataBuilder.class);
	
	public PluginMetadata build(String rsrc) {
		return build(JsonUtils.readResource(rsrc));
	}
	Resolver resolver;
	
	public PluginMetadataBuilder withResolver(Resolver r) {
		this.resolver = r;
		return this;
	}
	
	/**
	 * Builds metadata for a plug-in from an input stream
	 * of YML content.
	 * 
	 * @param in JSON input stream to read 
	 * @param resolver resolves a name to a reference
	 * @return a plug-in descriptor
	 */
	public PluginMetadata build(InputStream in) {
		return build(JsonUtils.readStream(in));
	}
	
	/**
	 * 
	 * @param json
	 * @param resolver
	 * @return
	 */
	public PluginMetadata build(ObjectNode json) {
		try {
			assertProperty(json, PROPERTY_NAME, false);
			assertProperty(json, PROPERTY_CLASS, false);
			String name = json.get(PROPERTY_NAME).asText();
			logger.info("building plugin descriptor " + name);
		
			String clsname = json.get(PROPERTY_CLASS).asText();
			PluginMetadata meta = 
					ObjectFactory.newInstance(clsname, PluginMetadata.class);
			meta.setName(name);
			meta.setDescription(json.path(PROPERTY_DESCRIPTION).asText());
			assertProperty(json, PROPERTY_METRICS, false);
			String metricsName = json.path("metrics").asText();
			if (resolver != null) {
				Metrics m = resolver.resolve(metricsName, Metrics.class);
				meta.setMetrics(m);
			} else {
				throw new IllegalStateException("metrics [" + metricsName + "]"
						+ " can not be resolved because no resolver has been set");
			}
			
			
						

			/**
			 * let individual plug-in configures themselves
			 * Individual plug-in knows its own configuration
			 */
			meta.configure(json);			
			
			return meta;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	
	
	public static void assertProperty(JsonNode json, String key, boolean array) {
		if (!json.has(key)) {
			throw new IllegalArgumentException("missing property [" + key + "]"
					+ " available fields are " + fieldNames(json));
		}
		if (array && !json.get(key).isArray()) {
			throw new IllegalArgumentException("property [" + key + "] is not an array");
		}
	}
	
	static List<String> fieldNames(JsonNode json) {
		Iterator<String> fields = json.fieldNames();
		List<String> names = new ArrayList<>();
		while (fields.hasNext()) {
			names.add(fields.next());
		}
		return names;
	}

	@Override
	public Class<PluginMetadata> getType() {
		return PluginMetadata.class;
	}
	
}
