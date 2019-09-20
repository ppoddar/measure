package com.nutanix.bpg;

import java.sql.Connection;
import java.util.Collection;

import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.Metrics;

public interface Repository {

	public static final String PROP_DATABASE_NAME = "database.name";
	public static final String PROP_DATABASE_HOST = "database.host";
	public static final String PROP_URL           = "database.url";
	public static final String PROP_DRIVER        = "database.driver";
	public static final String PROP_USER          = "database.user";
	public static final String PROP_PASSWORD      = "database.password";

	public static final String CATALOG_DATABASE_URL = "catalog.database.url";
	
	Connection getConnection();
	/**
	 * records a database.
	 * @param db a database
	 * @param overwrite whether t overwrite  
	 * database of same name, if exists
	 * 
	 */
	public void addDatabase(Database db, boolean overwrite);

	/**
	 * gets the database of given name.
	 * @param name name of a database
	 * @return a database 
	 * @throws RuntimeException if no database of given name
	 * exists.
	 */
	Database getDatabase(String name);
	
	Metrics getMetrics(String name);
	Collection<Metrics> getMetrices();
	
	/**
	 * gets all database known to this system.
	 * @return a collection. must not be empty 
	 * or null.
	 */
	Collection<Database> getDatabases();
	
}
