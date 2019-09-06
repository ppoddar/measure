package com.nutanix.bpg.measure.script;

import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.PluginMetadata;

/**
 * parses a stream to create measurement.
 * 
 * @author pinaki.poddar
 *
 */
public interface MeasurementParser  {
	/**
	 * parse given line to create a row.
	 * @param meta identifies the tokens in given line.
	 * @param line a string to be parsed
	 * @return a row which is essentially a set of
	 * name-value pairs.
	 */
	Measurement createMeasurement(PluginMetadata meta, String line);
}
