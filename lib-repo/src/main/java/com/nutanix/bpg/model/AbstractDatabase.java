package com.nutanix.bpg.measure.model;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractDatabase implements Database {
	final DatabaseKind kind;
	private String name;
	private String description = "";
	private String host;
	private int    port;
	private String user;
	private String password;
	private Catalog<Metrics> metricsCatalog;
	protected Logger logger = LoggerFactory.getLogger(AbstractDatabase.class);
	
	public DatabaseKind getKind() {
		return kind;
	}

	public AbstractDatabase(DatabaseKind kind) {
		super();
		this.kind = kind;
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	public void setPassword(String pwd) {
		this.password = pwd;
	}
	
	
	
	public String toString() {
		return getKind() + ":" + getName() + "@" + getHost() + ":" + getPort();
	}

	protected String getPassword() {
		return password;
	}
	
	@JsonIgnore
	@Override
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(
					getUrl(), getUser(), getPassword());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public String getCatalog() {
		return null;
	}
	
	public String getSchema() {
		return null;
	}
	
	public String getTableNamePattern() {
		return null;
	}
	
	/**
	 * gets all metrics (statistics table) in the database.
	 * The tables in the database are analyzed and a 
	 * {@link Metrics metrics} is created for each table.
	 * Each columns in a table forms a  {@link MetricsDimension 
	 * dimension} in the {@link Metrics metrics}.
	 *  
	 * 
	 */
	public Catalog<Metrics> getMetrics() {
		if (metricsCatalog != null) {
			return metricsCatalog;
		} else {
			metricsCatalog = MetricsFactory.build(this);
		}
		return metricsCatalog;
	}
	
	
	public Metrics getMetrics(String name) {
		return getMetrics().get(name);
	}
}
