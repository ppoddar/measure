package com.nutanix.bpg.measure.script;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measure.model.PluginMetadata;

/**
 * parses a line to create measurement.
 * Each measurement is separated by <code>|</code> by deafult.
 *  
 * @author pinaki.poddar
 *
 */
public class TokenParser implements MeasurementParser {
	private static final Logger logger = LoggerFactory.getLogger(TokenParser.class);
	private String tokenSeparator = "\\|";
	
	public void setSeparator(String sep) {
		tokenSeparator = sep;
	}
	
	/**
	 * parses a string by separating spaces 
	 * @param line line to parse
	 * @return a measurement
	 */
	public Measurement createMeasurement(PluginMetadata meta, String line) {
		if (line.isEmpty()) return null;
		Measurement row = new Measurement(meta.getMetrics());
		List<String> tokens = tokenize(line.trim(), tokenSeparator);
		if (tokens.size() != meta.getMetrics().getDimensionCount()) {
			throw new IllegalStateException("Received data [" 
		    + line + "] is tokenized into " + tokens.size()
		    + " tokens (using separator " + tokenSeparator + "),"
		    + " but current " + meta + " expects " + 
		    + meta.getMetrics().getDimensionCount() + ":"
		    + meta.getMetrics().getDimensionNames() + " metrics to be measured");
		}
		logger.debug("received " + line + " separated into " + tokens.size() + " tokens");
		for (int i = 0; i < meta.getMetrics().getDimensionCount(); i++) {
			MetricsDimension dim = meta.getMetrics().getDimension(i);
			logger.debug("\t " + dim + "=" + tokens.get(i));
			row.putValue(dim, tokens.get(i));
		}
		return row;	
	}
	
	
	List<String> tokenize(String line, String sep) {
		logger.debug("line to parse [" + line + "]");
		String[] tokens = line.split(sep);
		logger.debug("line parses into " + tokens.length + " tokens by separtor " + sep);
		List<String> result = new ArrayList<String>();
		for (String t : tokens) {
			if (t == null || t.trim().isEmpty()) continue;
			logger.debug("token " + result.size() + ":" + t);
			result.add(t.trim());
		}
		return result;
	}

}
