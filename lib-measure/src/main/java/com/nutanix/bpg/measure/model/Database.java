package com.nutanix.bpg.measure.model;

import java.sql.Connection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Description of a database from statistics/metrics
 * collection perspective.
 * 
 * @author pinaki.poddar
 *
 */

@JsonDeserialize(as=PostgresDatabase.class)
public interface Database extends Named {
	/**
	 * gets the kind of databse.
	 * A kind specifies default properties of database.
	 * Every databse has a kind.
	 * 
	 * @return
	 */
	DatabaseKind getKind();
	
	/**
	 * gets name of a database.
	 * Database name must be unique.
	 */
	String getName();
	
	
	/**
	 * gets description of a database.
	 */
	String getDescription();
	
	/**
	 * gets IP address name of database server host.
	 * The name/IP address must be resolved by DNS
	 * for connecting to the databs eremotely.
	 * 
	 * @return
	 */
	String getHost();
	
	/**
	 * gets listen port of database server host.
	 * 
	 * @return
	 */
	int getPort();
	
	/**
	 * gets database user name.
	 * 
	 * @return
	 */
	String getUser();
	String getUrl();
	
	/**
	 * gets database catalog for statistics tables.
	 * can return null.
	 * 
	 * @return
	 */
	String getCatalog();
	/**
	 * gets database schema for statistics tables.
	 * can return null.
	 * 
	 * @return
	 */
	String getSchema();

	Connection getConnection();
	
	/**
	 * gets all metrics known to this database
	 * @return
	 */
	Catalog<Metrics> getMetrics();
	/**
	 * gets a metrics known to this database
	 * by given name
	 * @return
	 */
	Metrics getMetrics(String name);
	
	/**
	 * gets pattern for statistics tables.
	 * can return null.
	 * 
	 * @return
	 */
	String getTableNamePattern();
	/**
	 * affirms is given table is name for a
	 * statistics table.
	 * 
	 * @return
	 */
	boolean isStatisticsTable(String tableName);
	
	/**
	 * gets types of statistics table.
	 * 
	 * @return
	 */
	String[] getStatisticsTableTypes();
	

}