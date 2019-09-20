package com.nutanix.bpg.measure.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.model.MetricsDimension;
import com.nutanix.bpg.sql.SQLQueryExcutor;

/**
 * A measurement is a vector of values associated
 * with context. The values are defined by 
 * a {@link Metrics}.
 * <br>
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class Measurement implements Serializable {
	private String id;
	private String ctxType;
	private String ctx;
	private long startTime;
	private long endTime;
	private Metrics metrics;
	
	/** all values are stored as opaque object in memory
	and database specific type in database. 
	A value represents a {@link MetricsDimension dimension}
	which has metadata for type conversion
	*/
	private Map<String, Object> values = new HashMap<String, Object>();
	
	/**
	 * Create a measurement for given metrics.
	 * @param m a metrics that defines the dimensions
	 * of a measurement
	 */
	public Measurement(Metrics m) {
		assert m != null;
		this.id = UUID.randomUUID().toString();
		this.metrics = m;
		this.startTime = System.currentTimeMillis();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Metrics getMetrics() {
		return metrics;
	}
	
	public void setMetrics(Metrics m) {
		metrics = m;
	}
	
	public String getContextType() {
		return ctxType;
	}
	
	public String getContext() {
		return ctx;
	}
	
	/**
	 * sets given string as context.
	 * @param ctx
	 */
	public void setContext(String type, String ctx) {
		this.ctxType = type;
		this.ctx = ctx;
	}
	
	/**
	 * sets a time stamp for this measurement.
	 * The time stamp is assumed to be in GMT.
	 * It is callers responsibility to provide
	 * a time stamp in GMT.
	 * @param ts
	 */
	public void setStartTime(long ts) {
		this.startTime = ts;
	}
	public void setEndTime(long ts) {
		this.endTime = ts;
	}
	
	/**
	 * gets a time stamp in GMT 
	 * @return
	 */
	public long getStartTime() {
		return startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	
	/**
	 * set value for given {@link MetricsDimension}
	 * The value is given as a string, but set as a typed value
	 * appropriate to given {@link MetricsDimension dimension}.
	 * <p>
	 * The given string value is converted to {@link MetricsDimension#getValueType() 
	 * value type} of given {@link MetricsDimension dimension}.
	 * 
	 * @param dim dimension must not be null. must
	 * exist is associated metrics 
	 * 
	 * @param valueString value as a string
	 */
	public void putValue(MetricsDimension dim, Object value) {
		Objects.requireNonNull(dim, "dimension can not be null");
		values.put(dim.getName(), value);
	}
	
	public void putValue(String name, Object value) {
//		if (!metrics.hasDimension(name)) {
//			throw new IllegalArgumentException("can not set value for unknown dimension [" + name + "]"
//					+ " known dimensions are " + metrics.getDimensionNames());
//		}
		values.put(name, value);
	}

	/**
	 * gets value of a dimension by given name.
	 * 
	 * @param name
	 * @exception if name is not a dimension in metrics
	 * represented this receiver.
	 * @return
	 */
	public Object getValue(String name) {
		if (!metrics.hasDimension(name)) {
			throw new IllegalArgumentException("can not get value"
					+ " for [" + name + "] as " + metrics
					+ " does not contain any dimension named [" + name + "]"
					+ " Known dimensions are " + metrics.getDimensionNames());
		}
		return values.get(name);
	}
	
	public Map<String, Object> getValues() {
		return values;
	}
	/**
	 * Gets the values from a result set.
	 * The result set columns have the same name as
	 * metric dimensions.
	 * If result set is missing a dimension, corresponding
	 * value is not populated
	 * @param rs result set.
	 * @throws SQLException
	 */
	public void populateFromResultSet(ResultSet rs) throws SQLException {
		for (MetricsDimension d : metrics) {	
			Object obj = SQLQueryExcutor.getValue(d.getName(), rs);
			values.put(d.getName(), obj);
		}
	}
	
	public boolean isAfter(Measurement other) {
		return (this.getStartTime() >= other.getEndTime());
	}
	
	public boolean isBefore(Measurement other) {
		return (this.getStartTime() <= other.getEndTime());
	}
	
	public String details() {
		return metrics.getName() + ":" + values.toString();
	}
	public String toString() {
		return "measurement:" + metrics.getName() + "@" + getStartTime();
	}
	
	public Measurement clone() {
		Measurement clone = new Measurement(this.metrics);
		clone.id = this.id;
		clone.ctxType = this.ctxType;
		clone.ctx = this.ctx;
		clone.startTime = this.startTime;
		clone.endTime = this.endTime;
		clone.values = new HashMap<>(this.values);
		return clone;
	}
	

}
