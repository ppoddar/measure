package com.nutanix.bpg.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class JsonUtils {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	public static int getInt(JsonNode json, String p, int def) {
		return  (json.has(p)) ? json.get(p).asInt() : def;
	}
	
	public static String getString(JsonNode json, String p, String def) {
		return  (json.has(p)) ? json.get(p).asText() : def;
	}
	
	public static JsonNode getObject(JsonNode json, String p, JsonNode def) {
		return  (json.has(p)) ? json.get(p) : def;
	}
	
	public static JsonNode getObject(JsonNode json, String p) {
		if (json.has(p)) {
			return json.get(p);
		}
		throw new RuntimeException("missing property [" + p + "]"
				+ " available properties " + propertyNames(json));
	}
	
	

	
	public static Map<String, String> getMap(JsonNode json, String p) {
		return getMap(json, p, null);
	}

	/**
	 * converts a property value to a map
	 * <pre>
	 * { 
	 *    ...
	 *    "x": {
	 *       "a": "1",
	 *       "b": "2"
	 *     },
	 *     ...
	 * }
	 * </pre>
	 * will be converted to a map
	 * 
	 * @param json
	 * @param p
	 * @param def
	 * @return
	 */
	public static Map<String, String> getMap(JsonNode json, 
			String p, Map<String, String> def) {
		if (json.has(p)) {
			JsonNode map = json.get(p);
			Iterator<String> fields = map.fieldNames();
//			logger.debug("getMap() got json node " +  map.getNodeType() 
//			+ " for property [" + p + "] with fields " 
//			+ propertyNames(map));
			Map<String, String> result = new HashMap<>();
			while (fields.hasNext()) {
				String field = fields.next();
				JsonNode value = map.get(field);
				if (value.isTextual()) {
					result.put(field, value.asText());
				} else if (value.isNumber()) {
					result.put(field, ""+value.asInt());
				} else {
					throw new IllegalArgumentException("expected [" + p + "]"
							+ " to be name-value pairs. "
							+ " But value for [" + value + "] for field [" + field + "]"
							+ " is not textual");
				}
			}
			logger.debug("JSON map [" + p + "] =" + result);
			return result;
			
			//return new ObjectMapper().convertValue(map, Map.class);
//			TypeReference<HashMap<String,String>> typeRef 
//        		= new TypeReference<HashMap<String,String>>() {};
//        	return new ObjectMapper().convertValue(map, typeRef);
		} else if (def != null) {
			return def;
		} else {
			throw new RuntimeException("missing property [" + p + "]"
				+ " available properties " + propertyNames(json));
		}
	}

	
	public static JsonNode getArray(JsonNode json, String p, JsonNode def) {
		return  (json.has(p)) ? json.get(p) : def;
	}
	
	public static JsonNode getArray(JsonNode json, String p) {
		if (json.has(p)) {
			return json.get(p);
		}
		throw new RuntimeException("missing property [" + p + "]"
				+ " available properties " + propertyNames(json));
	}

	
	public static String getString(JsonNode json, String p) {
		if (json.has(p)) {
			return json.get(p).asText();
		}
		throw new RuntimeException("missing property [" + p + "]"
				+ " available properties " + propertyNames(json));
	}
	
	public static int getInt(JsonNode json, String p) {
		if (json.has(p)) {
			return json.get(p).asInt();
		}
		throw new RuntimeException("missing property [" + p + "]"
				+ " available properties " + propertyNames(json));
	}
	
	public static long getLong(JsonNode json, String p) {
		if (json.has(p)) {
			return json.get(p).asLong();
		}
		throw new RuntimeException("missing property [" + p + "]"
				+ " available properties " + propertyNames(json));
	}



	
	
	
	/**
	 * reads given stream of YML to a JsonNode. 
	 * @param in
	 * @return
	 */
	public static JsonNode readStream(InputStream in) {
		try {
			YAMLFactory yaml = new YAMLFactory();
			ObjectMapper mapper = new ObjectMapper(yaml);
			JsonNode json = mapper.readTree(in);
			return json;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static JsonNode assertProperty(JsonNode json, String key) {
		assertProperty(json, key, false);
		return json.get(key);
	}
	
	public static JsonNode assertProperty(JsonNode json, String key, boolean array) {
		if (!json.has(key)) {
			throw new IllegalArgumentException("missing property [" + key + "] in \n" + json);
		}
		if (array && !json.get(key).isArray()) {
			throw new IllegalArgumentException("property [" + key + "] is not an array in " + json);
		}
		return json.get(key);
	}
	/**
	 * reads a JSON/YAML file from given url.
	 * 
	 * @param url if no protocol then interprets as 
	 * file system path relative to current working directory.
	 * @param yaml if true parses YAML
	 * @return JSONNode read from URL
	 * 
	 */
	public static JsonNode readResource(URL url, boolean yaml) {
		try {
			ObjectMapper mapper = yaml
				? new ObjectMapper(new YAMLFactory())
				: new ObjectMapper();
			JsonNode json = mapper.readTree(url.openStream());
			if (json == null) {
				throw new RuntimeException("read null from " + url);
			}
			return json;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static List<String> propertyNames(
			JsonNode json) {
		return propertyNames(json, false);
	}

	
	/**
	 * get property names of a JSON.
	 * @param json
	 * @return
	 */
	public static List<String> propertyNames(
			JsonNode json, boolean sectionOnly) {
		List<String> names = new ArrayList<String>();
		Iterator<String> fields = json.fieldNames();
		while (fields.hasNext()) {
			String f = fields.next();
			JsonNode node = json.get(f);
			if (sectionOnly) {
				if (node.isValueNode()) continue;
			} 
			names.add(f);
		}
		return names;
	}

	/**
	 * Creates Properties from JSON.
	 * 
	 * @param json
	 */
	public static Properties toProperties(JsonNode json) {
		Properties props = new Properties();
		buildProperties(null, json, props);
		return props;
	}
	
	static void buildProperties(
			String prefix,
			JsonNode json,
			Properties props) {
		Iterator<String> fields = json.fieldNames();
		while (fields.hasNext()) {
			String fieldName = fields.next();
			JsonNode value = json.get(fieldName);
			String key = fieldName;
			if (prefix != null) {
				key = prefix + "." + key;
			}
			if (value.isValueNode()) {
				props.put(key, value.asText());
			} else if (value.isObject()) {
				
				buildProperties(key, value, props);
			} else if (value.isArray()) {
				
			}
		}
	}
	
	/**
	 * Merge two JSON
	 * @param mainNode
	 * @param updateNode
	 * @return
	 */
	public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {

	    Iterator<String> fieldNames = updateNode.fieldNames();
	    while (fieldNames.hasNext()) {

	        String fieldName = fieldNames.next();
	        JsonNode jsonNode = mainNode.get(fieldName);
	        // if field exists and is an embedded object
	        if (jsonNode != null && jsonNode.isObject()) {
	            merge(jsonNode, updateNode.get(fieldName));
	        }
	        else {
	            if (mainNode instanceof ObjectNode) {
	                // Overwrite field
	                JsonNode value = updateNode.get(fieldName);
	                ((ObjectNode) mainNode).set(fieldName, value);
	            }
	        }

	    }

	    return mainNode;
	}


}
