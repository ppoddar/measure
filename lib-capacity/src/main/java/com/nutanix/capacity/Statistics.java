package com.nutanix.capacity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.nutanix.capacity.impl.SimpleStatistics;

@JsonSubTypes({
	@JsonSubTypes.Type(SimpleStatistics.class)
})
public interface Statistics {
	String getName();
	void addSample(double sample);
	int getSize();
	double getMean();
	double getVariance();
	double getStandardDevitaion();
	double getCoefficientOfVariance();
	Statistics add(Statistics other);
}
