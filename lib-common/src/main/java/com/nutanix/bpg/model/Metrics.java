package com.nutanix.bpg.model;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.bpg.utils.IndexibleMap;
import com.nutanix.bpg.utils.Named;

/**
 * A metrics is named, ordered group of 
 * {@link MetricsDimension dimension}.
 * <p>
 * A possible metrics is statistics collector tables
 * in Postgres database.
 * <p>
 * 
 * @author pinaki.poddar
 *
 */
public class Metrics implements Named, Iterable<MetricsDimension> {
	private String name;
	private String description;
	private IndexibleMap<MetricsDimension> dimensions = new IndexibleMap<MetricsDimension>();
	private static Logger logger = LoggerFactory.getLogger(Metrics.class);
	
	@JsonCreator
	public Metrics(@JsonProperty("name") String name) {
		setName(name);
	}
	
	
	/**
	 * gets name of the metric
	 * @return name of the metric
	 */
	public String getName() {
		return name;
	}
	
	
	void setName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("null/empty metrics name");
		}
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean hasDimension(String name) {
		return dimensions.has(name);
	}

	public void addDimension(MetricsDimension dim) {
		logger.trace("add " + dim + " to " + this);
		dimensions.put(dim);
	}

	/**
	 * gets number of dimensions measured by this plug-in.
	 * 
	 * @return non-zero positive
	 */
	@JsonIgnore
	public int getDimensionCount() {
		return dimensions.size();
	}
	
	public MetricsDimension getDimension(int i) {
		return dimensions.getByIndex(i);
	}
	
	public MetricsDimension getDimension(String name) {
		return dimensions.getByName(name);
	}
	
	public List<String> getDimensionNames() {
		return dimensions.keys();
	}

	public int size() {
		return dimensions.size();
	}
	
	@Override
	public Iterator<MetricsDimension> iterator() {
		return dimensions.iterator();
	}
	
	public List<MetricsDimension> getDimensions() {
		return dimensions.values();
	}
	
	public String toString() {
		return "metrics:" + getName() + " (dim:" + getDimensionCount() + ")";
	}
}
