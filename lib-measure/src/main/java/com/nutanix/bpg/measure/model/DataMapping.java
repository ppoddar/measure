package com.nutanix.bpg.measure.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.utils.ClasspathUtils;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.model.MetricsDimension;

/**
 * maps a name to a {@link MetricsDimension}.
 * A {@link MetricsDimension} is, by default.
 * a VARCHAR column. 
 * MetricsDimension are declaread in a {@link Metrics}.
 * However, columns that are not part of a {@link Metrics}
 * and are requires special cast.
 *   
 * @author pinaki.poddar
 *
 */
public  class DataMapping {
	public static String MAPPING_FILE_NAME = "datamapping.yml";
	public static Map<String, MetricsDimension>
		mappedColumns = new HashMap<>();
	private static Logger logger = LoggerFactory.getLogger(DataMapping.class);
	
	static {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			MetricsDimension[] dims =
			mapper.readValue(ClasspathUtils.getInputStream(MAPPING_FILE_NAME), 
						MetricsDimension[].class);
			for (MetricsDimension dim: dims) {
				mappedColumns.put(dim.getName(), dim);
			}
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		} 
	}
	
	public static String getTableForMetrics(Metrics metrics) {
		if (metrics == null) {
			throw new IllegalArgumentException("can not get tabnle name for null metrics");
		}
		return "MEASUREMENT_" + metrics.getName().toUpperCase();
	}
	
	
	public static MetricsDimension getMappedDimension(String name) {
		if (!mappedColumns.containsKey(name)) {
			logger.warn("column [" + name + "] is not mapped"
					+ " mapped columns are " + mappedColumns.keySet());
			return new MetricsDimension(name);
		}
		return mappedColumns.get(name);
	}

}
