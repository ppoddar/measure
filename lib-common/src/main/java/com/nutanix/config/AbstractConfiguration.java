package com.nutanix.config;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.utils.JsonUtils;

public abstract class AbstractConfiguration implements Configuration {
	private URI location;
	private JsonNode data;
	private static Logger logger = LoggerFactory.getLogger(AbstractConfiguration.class);
	protected Configuration init(JsonNode json) {
		this.data = json.deepCopy();
		return this;
	}
	
	protected abstract AbstractConfiguration newInstance(JsonNode data);
	
	@Override
	public String getString(String key) {
		return getString(key, null);
	}
	
	@Override
	public String getOptionalString(String key) {
		if (data.has(key)) {
			return data.get(key).asText();
		} else {
			return null;
		}
	}


	@Override
	public String getString(String key, String def) {
		if (data.has(key)) {
			return data.get(key).asText();
		} else if (def == null) {
			throw new IllegalArgumentException("missing property [" + key + "]"
					+ " in " + this
					+ " available properties are "  
					+ JsonUtils.propertyNames(data));
		} else {
			return def;
		}
	}
	
	public URI getLocation() {
		return location;
	}
	
	public Path getPath() {
		return Paths.get(location);
	}

	@Override
	public Configuration getSection(String key) {
		if (data.has(key)) {
			JsonNode section = data.get(key);
			if (section.isObject()) {
				Configuration derived = newInstance(section)
						.setLocation(getLocation());
				logger.debug("derive section [" +  key + "] :" + derived);
				return derived;
			} else {
				Path anchor = Paths.get(getLocation()).getParent();
				URI relativeURI = anchor.resolve(section.asText())
						.toUri();
				Configuration derived = ConfigurationFactory.newConfiguration(relativeURI);
				logger.debug("derive section [" +  key + "] :" + derived);
				return derived;
			}
		} else {
			throw new IllegalArgumentException("missing section [" + key + "]"
					+ " in " + this 
					+ " available properties are "  
					+ JsonUtils.propertyNames(data));
		}
	}
	
	public AbstractConfiguration setLocation(URI uri) {
		this.location = uri;
		return this;
	}
	
	public final JsonNode asJson() {
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public final Map<String, String> asMap() {
		ObjectMapper mapper = new ObjectMapper();
		return (Map<String, String>)mapper.convertValue(data, Map.class);
	}
	
	public String toString() {
		return "configuration@ " + getLocation();
	}
	
	@Override
	public Path resolvePath(String key) {
		String path = getString(key);
		return getPath().getParent()
				.resolve(path).normalize();
	}
}
