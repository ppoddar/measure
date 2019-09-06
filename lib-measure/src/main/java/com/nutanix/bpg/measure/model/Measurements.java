package com.nutanix.bpg.measure.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.bpg.measure.utils.Conversion;
import com.nutanix.bpg.measure.utils.Statistics;

/**
 * A list of measurements. The list is homogeneous i.e.
 * each measurement in the list has same {@link Metrics}.
 * 
 * @author pinaki.poddar
 *
 */
public class Measurements implements Iterable<Measurement>{
	private final List<Measurement> list;
	private Metrics metrics;
	private final Map<String, Statistics> ranges;
	
	public Measurements() {
		list = new ArrayList<Measurement>();
		this.ranges = new LinkedHashMap<String, Statistics>();
	}
	
	public Measurement getMeasurement(int i) {
		return list.get(0);
	}
	
	@JsonProperty("data")
	public List<Measurement> getMeasurements() {
		return list;
	}
	
	
	public Map<String, Statistics> getRanges() {
		return ranges;
	}

	
	public long getStartTime() {
		return getMeasurement(0).getStartTime();
	}
	
	public long getEndTime() {
		if (isEmpty()) return -1;
		int N = getSize();
		return getMeasurement(N-1).getEndTime();
	}
	
	public Metrics getMetrics() {
		if (metrics == null) {
			throw new IllegalStateException("metrics is not set");
		}
		return metrics;
	}
	
	public void setMetrics(Metrics m) {
		metrics = m;
	}
	
	public int getSize() {
		return list.size();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public Statistics getRange(String name) {
		return ranges.get(name);
	}
	
	public Statistics getRange(MetricsDimension dim) {
		return ranges.get(dim.getName());
	}
	
	public void addMeasurement(Measurement m) {
		if (m == null) {
			throw new IllegalArgumentException("can not add null measurment");
		}
		if (metrics != null) {
			if (!metrics.equals(m.getMetrics())) {
				throw new IllegalArgumentException("can not add measurement "
						+ " of " + m.getMetrics() + " to this homogeneous set of measurement"
						+ " with " + this.getMetrics());
			}
		}
		if (isEmpty()) {
			metrics = m.getMetrics();
			initRanges(metrics);
		}
		if (!isEmpty() && !this.metrics.equals(m.getMetrics())) {
			throw new IllegalArgumentException("can not add " + m
					+ " becuase its metrics " + m.getMetrics()
					+ " is not same as " + this.metrics
					+ " Measurements is a homogeneous set");
		}
		list.add(m);
		for (MetricsDimension dim : metrics) {
			String name = dim.getName();
			Object value = m.getValue(name);
			try {
				double d = Conversion.convert(value, Double.class);
				this.ranges.get(name).update(d);
			} catch (Exception ex) {
				
			}
		}
	}
	
	void initRanges(Metrics metrics) {
		for (MetricsDimension dim : metrics) {
			this.ranges.put(dim.getName(), new Statistics());
		}
	}
	

	@Override
	public Iterator<Measurement> iterator() {
		return list.iterator();
	}
	
}
