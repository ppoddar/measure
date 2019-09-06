package com.nutanix.bpg.measure.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measurer.dao.MeasurementDAO;

/**
 * a dynamic table is created for a {@link Measurement}.
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class DynamicTable {

	private final String tableName;
//	private final Metrics metrics;
	
	// punctuation for SQL
	public static String OPEN_BRACKET   = "(";
	private static String CLOSE_BRACKET = ")";
	private static String SPACE         = " ";
	private static String COMMA         = ",";
	

	public DynamicTable(String name, Metrics metrics) {
		this.tableName = name;
//		this.metrics = metrics;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Creates a table with dynamic schema 
	 * if it does not exist.
	 * @param con a connection
	 * @return
	 * @throws SQLException
	 */
	public String create(Connection con, Metrics m) throws SQLException {
		String ddl = createTableDDL(m);
		PreparedStatement ps = con.prepareStatement(ddl);
		con.setAutoCommit(false);
		try {
			ps.execute();
		} catch (Exception ex) {
			throw new RuntimeException("failed to create dynamic table "
					+ tableName + " with DDL [" + ddl + "] due to", ex);
		}
		con.commit();
		
		return ddl;
		
	}
	
	/**
	 * 
	 * @param tableName
	 * @param metrics
	 * @return
	 * @throws SQLException
	 */
	String createTableDDL(Metrics m) throws SQLException {
		String ddl = "CREATE TABLE IF NOT EXISTS " 
	         + tableName
	         + SPACE + OPEN_BRACKET
		     + MeasurementDAO.MEASUREMENT_ID         + " VARCHAR PRIMARY KEY,"
		     + MeasurementDAO.MEASUREMENT_METRICS    + " VARCHAR,"
		     + MeasurementDAO.MEASUREMENT_START_TIME + " BIGINT,"
		     + MeasurementDAO.MEASUREMENT_END_TIME   + " BIGINT,"
		     + MeasurementDAO.MEASUREMENT_CONTEXT_TYPE    + " VARCHAR,"
		     + MeasurementDAO.MEASUREMENT_CONTEXT    + " VARCHAR,"
		     + joinColumnNames(m, true)
		     + CLOSE_BRACKET;
		
		return ddl;
	}
	
	/**
	 * Joins all columns in given result set separated
	 * by COMMA. If type is true, SQL type of each column
	 * is also written
	 *  
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	String joinColumnNames(Metrics metrics, boolean type) throws SQLException {
		String sql = "";
		int N = metrics.getDimensionCount();
		for (int i = 0; i < N; i++) {
			MetricsDimension dim = metrics.getDimension(i);
			String columnDescriptor = dim.getName();
			if (type) {
				columnDescriptor +=  SPACE + dim.getSqlTypeName();
			}
			sql += columnDescriptor;
			if (i != N-1) {
				sql +=  COMMA;
			}
		}
		return sql;
	}
}
