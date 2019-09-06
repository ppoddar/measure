package com.nutanix.bpg.measure;

import com.nutanix.bpg.measure.model.Measurement;
/**
 * a functional interface to take a measurement.
 * 
 * @author pinaki.poddar
 *
 */
public interface MeasuremementTaker {
	/**
	 * take a measurement,
	 * 
	 * @return a measurement
	 * 
	 * @throws Exception if anything goes wrong
	 */
	Measurement takeMeasurement() throws Exception;

}
