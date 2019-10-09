package com.nutanix.capacity.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Statistics;
import com.nutanix.capacity.Utilization;

/**
 * 
 * @author pinaki.poddar
 *
 */
public class DefaultUtilization implements Utilization {
	private Map<ResourceKind, Statistics> stats;
	
	/**
	 * create an utilization with each kind of utilization
	 * being 0
	 */
	public DefaultUtilization() {
		stats = new HashMap<ResourceKind, Statistics>();
		for (ResourceKind kind : ResourceKind.values()) {
			stats.put(kind, new SimpleStatistics());
		}
	}

	/**
	 * put utilization factor of given kind as given
	 * value. If an utilization for given kind exists,
	 * the given value is added to existing average.
	 */
	public void put(ResourceKind kind, Double d) {
		stats.get(kind).addSample(d);
	}
	

	@Override
	public Utilization accumulate(Utilization other) {
		for (ResourceKind k : other) {
			Statistics s2 = other.get(k);
			Statistics s1 = this.get(k);
			this.stats.put(k, s1.add(s2));
		}
		return this;
	}

	@Override
	public Iterator<ResourceKind> iterator() {
		return stats.keySet().iterator();
	}

	@Override
	public Statistics get(ResourceKind kind) {
		return stats.get(kind);
	}


	@Override
	public String toString() {
		String s = String.format("average: %4.2f", getAverage());
		for (ResourceKind kind : stats.keySet()) {
			Statistics stat = get(kind);
			if (stat.getSize() > 0) {
				s += String.format(" %s: %4.2f ", kind, stat.getMean());
			}
		}
		
		return "utilization: " + s;
	}

	@Override
	public double getAverage() {
		double average = 0.0;
		double weight = 0.0;
		for (ResourceKind kind: stats.keySet()) {
			Statistics stat = get(kind);
			if (stat.getSize() == 0) continue;
			weight += 1.0;
			average += stat.getMean();
		}
		if (weight == 0) return 0.0;
		return average/weight;
	}

	@Override
	public double getWeightedAverage(Map<ResourceKind, Double> weights) {
		double average = 0.0;
		double weight = 0;
		for (ResourceKind kind: stats.keySet()) {
			Statistics stat = get(kind);
			if (stat.getSize() == 0) continue;
			double w = weights.containsKey(kind) ? weights.get(kind) : 0;
			weight += w;
			average += w*stat.getMean();
		}
		return average/weight;
	}


}
