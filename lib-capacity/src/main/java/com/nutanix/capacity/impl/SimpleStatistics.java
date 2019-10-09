package com.nutanix.capacity.impl;

import com.nutanix.capacity.Statistics;

public class SimpleStatistics implements Statistics {
	String name;
	int size;
	double sum;
	double sumSq;
	
	public SimpleStatistics() {
	}
	public SimpleStatistics(String name) {
		this.name = name;
	}
	
	
	@Override
	public void addSample(double sample) {
		size += 1;
		sum += sample;
		sumSq += (sample*sample);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public double getMean() {
		if (size == 0) return 0;
		return sum/size;
	}

	@Override
	public double getStandardDevitaion() {
		if (size == 0) return 0;
		if (size == 1) return 0;
		return Math.sqrt(getVariance());
	}
	
	@Override
	public double getCoefficientOfVariance() {
		if (size == 0) return 0;
		if (size == 1) return 1;
		return getStandardDevitaion()/getMean();
	}

	
	public double getVariance() {
		if (size == 0) return 0.0;
		if (size == 1) return 0.0;
		return  (sumSq - getMean()*getMean())/size;
	}

	@Override
	public Statistics add(Statistics stat) {
		SimpleStatistics addition = new SimpleStatistics();
		SimpleStatistics other = SimpleStatistics.class.cast(stat);
		addition.size = this.size + other.size;
		addition.sum  = this.sum  + other.sum;
		addition.sumSq  = this.sumSq  + other.sumSq;
		return addition;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public Statistics setName(String name) {
		this.name = name;
		return this;
	}
	
	public String toString() {
		return getName()
			+ " size:" + getSize()
			+ " mean:" + getMean()
			+ " stdev:" + getStandardDevitaion()
			+ " var:" + getCoefficientOfVariance();
	}

}
