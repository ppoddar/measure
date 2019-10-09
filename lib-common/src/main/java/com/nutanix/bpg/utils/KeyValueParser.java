package com.nutanix.bpg.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses key-value pairs to a map.
 * 
 * @author pinaki.poddar
 *
 */
public class KeyValueParser  {
	private static final String TOKEN_SEPARATOR    = "\\s+,\\s+";
	private static final Logger logger = LoggerFactory.getLogger(KeyValueParser.class);
	/**
	 * parses a string to name-value pairs 
	 * @param line string has comma-separated
	 * list of name-value pairs of the form
	 * <code>name1=value1, name2=value2 ...</code>
	 * @return a map 
	 */
	public static Map<String, String> parse(String line) {
		Map<String, String> row = new HashMap<String, String>();
		if (line == null || line.trim().isEmpty())
			return row;
		String[] tokens = line.split(TOKEN_SEPARATOR);
		for (String token : tokens) {
			if (token.trim().isEmpty()) continue;
			int idx = token.indexOf('=');
			if (idx == -1) {
				logger.warn("token [" + token + "] in [" + line + "] "
						+ " does not parse as a key-value pair"
						+ " continue with other key-value pairs");
				continue;
			}
			String key   = token.substring(0,idx);
			String value = token.substring(idx+2);
			logger.debug("parsed key-value pair [" + key + "=" + value + "] from [" + line + "]");
			row.put(key, value);
		}
		return row;	
	}
}
