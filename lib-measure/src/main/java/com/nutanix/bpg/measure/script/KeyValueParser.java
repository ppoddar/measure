package com.nutanix.bpg.measure.script;

import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measure.model.PluginMetadata;

/**
 * Parses key-value pairs to a {@link Measurement measurement}.
 * 
 * @author pinaki.poddar
 *
 */
public class KeyValueParser implements MeasurementParser {
	private static final String TOKEN_SEPARATOR = "\\s+,\\s+";
	private static final String KEYVALUE_SEPARATOR = "\\s+:\\s+";
	
	/**
	 * parses a string to name-value pairs 
	 * @param line string has comma-separated
	 * list of name-value pairs of the form
	 * <code>name1:value1, name2:value2 ...</code>
	 * @return a measurement vector
	 */
	public Measurement createMeasurement(PluginMetadata meta, String line) {
		Measurement row = new Measurement(meta.getMetrics());
		String[] tokens = line.split(TOKEN_SEPARATOR);
		for (String token : tokens) {
			String[] nvPair = token.split(KEYVALUE_SEPARATOR);
			String key   = nvPair[0];
			String valueString = nvPair[1];
			MetricsDimension dim = meta.getMetrics().getDimension(key);
			row.putValue(dim, valueString);
		}
		return row;	
	}
}
