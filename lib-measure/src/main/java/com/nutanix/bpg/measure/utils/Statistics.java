package com.nutanix.bpg.measure.utils;
/**
 * statistics of double numbers.
 * 
 * @author pinaki.poddar
 *
 */
public class Statistics {
	private double sum;
	@SuppressWarnings("unused")
	private double sumSq;
	private double max = Double.POSITIVE_INFINITY;
	private double min = Double.NEGATIVE_INFINITY;
	private int count;
	
	public Statistics() {
		reset();
	}
	public void update(double sample) {
		count++;
		sum += sample;
		sumSq += (sample*sample);
		max = Math.max(max, sample);
		min = Math.min(min, sample);
	}
	
	public double getMean() {
		if (count == 0) return 0;
		return sum/count;
	}
	public double getStandardDeviation() {
		if (count == 0) return 0;
		double var = sumSq = getMean()*getMean();
		return Math.sqrt(var)/count;
	}
	public double getMax() {
		return max;
	}
	public double getMin() {
		return min;
	}
	
	public int getSize() {
		return count;
	}
	
	public void reset() {
		count = 0;
		sum   = 0.0;
		sumSq = 0.0;
		max = Double.POSITIVE_INFINITY;
		min = Double.NEGATIVE_INFINITY;
	}
	
	public String toString() {
		return String.format("%d: %f (%f)", 
			getSize(), getMean(), 
			getStandardDeviation());
	}
}
