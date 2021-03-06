package com.nutanix.bpg.measure.model;

import java.util.Objects;

import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;



/**
 * A snapshot is an ordered set of measurements.
 * <p>
 * Note:
 * Currently a snapshot is a set of <em>homegeneous</em>
 * measurements. But this may change in future.
 * <p>
 * Assumption:
 * The measurement data resides in 
 * target database D (e.g. Postgres Statistics 
 * Collector). 
 * A snapshot copies that data from D, adds
 * measurement context and saves it in a dynamic
 * table at Data Warehouse known to this  system.
 * The dynamic table has all the columns (with
 * same name and SQL type) of original table
 * plus additional columns to store context.
 * 
 * @author pinaki.poddar
 *
 */
public class Snapshot {
	private String id;
	private String name;
	private Database db;
	private Metrics metrics;
	int expectedMeasurementCount;
	
	
	public static final String CONTEXT_SNAPSHOT  = "snapshot";
	public static final String CONTEXT_BENCHMARK = "benchmark";
	/**
	 * create an empty snapshot
	 */
	public Snapshot() {
	}

	
	public String getId() {
		return id;
	}
	
	/**
	 * sets an identifier to this snapshot
	 * idempotently.
	 * 
	 * @param id an identifier
	 */
	public void setId(String id) {
		Objects.requireNonNull(id);
		if (id.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (this.id == null) {
			this.id = id;
		} else if (!this.id.equals(id)) {
			throw new IllegalArgumentException("can not assign "
					+ " identifer as an identifer " + this.id
					+ " is already assigned");
		}
	}
	
	public Database getDatabase() {
		return db;
	}
	
	public void setDatabase(Database db) {
		 this.db = db;
	}
	
	
	public Snapshot setMetrics(Metrics m) {
		metrics = m;
		return this;
	}
	
	public Metrics getMetrics() {
		return metrics;
	}
	

	/**
	 * gets name of this snapshot.
	 * @return name 
	 */
	public String getName() {
		return name;
	}
	
	public Snapshot setName(String name) {
		this.name = name;
		return this;
	}
	
	public void setExpectedMeasurementCount(int c) {
		expectedMeasurementCount = c;
	}
	
	public int getExpectedMeasurementCount() {
		return expectedMeasurementCount;
	}
	
	
	public String toString() {
		return "snapshot:" + getName();
	}
}
