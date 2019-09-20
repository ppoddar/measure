package com.nutanix.bpg.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.utils.Conversion;
import com.nutanix.bpg.utils.TypeUtils;
/**
 * Performs basic SQL operations.
 * @author pinaki.poddar
 *
 */
public class SQLQueryExcutor {
	private static Boolean showSQL = Boolean.getBoolean("show.sql");
	private static Logger logger = LoggerFactory.getLogger(SQLQueryExcutor.class);
	/**
	 * Populates an existing query
	 * @param ps
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement populate(
			PreparedStatement ps,
			Object[] params) throws SQLException {
		if (params == null || params.length == 0) return ps;
		if (params.length%2 != 0) {
			throw new IllegalArgumentException("Odd number (" + params.length + ")"
					+ " of elemements to poulate a prepared statement. "
					+ " Must be even numeberd elements "
					+ " of column name-value pairs");
		}
		for (int i = 0; i < params.length-1; i+=2) {
			Object value = params[i+1];
			setValueToPreparedStatement(ps, i/2+1, value);
		}
		return ps;
	}
	
	/**
	 * Prepares a SQL statement (but does not execute it)
	 * and populates with given parameters.
	 * @param con a connection to prepare a statement
	 * @param sql a SQL string with bind markers
	 * @param params an array of even number of elements.
	 * Elements at even index is a string -- for information -
	 * purpose. Next element at odd index is a value,
	 * that would be set at the prepared statement.
	 * <p><b>IMPORTANT</b>
	 * The values must of appropriate type i.e. if a column
	 * is of type TIMESTAMP in database, then corresponding
	 * value should be a java.sql.Timestamp object
	 * 
	 * The exception is INET type where value is of String,
	 * but Postgres server will check format of the string.
	 * 
	 * @return the prepared statement
	 * @throws SQLException if error during SQL operation
	 */
	public static PreparedStatement prepare(Connection con, 
			String sql,
			Object[] params) throws SQLException {
		if (showSQL) logger.info(sql);
		PreparedStatement ps = con.prepareStatement(sql);
		if (params == null || params.length == 0) return ps;
		if (params.length%2 != 0) {
			throw new IllegalArgumentException("Odd number (" + params.length + ")"
					+ " of elemements to prepare SQL statement "
					+ sql + " . Must be even numeberd elements "
					+ " of column name-value pairs");
		}
		
		for (int i = 0; i < params.length-1; i+=2) {
			Object value = params[i+1];
			setValueToPreparedStatement(ps, i/2+1, value);
		}
		return ps;
	}
	
	static void setJson(PreparedStatement ps, int idx, JsonNode value) throws SQLException {
		try {
			PGobject pgObj = new PGobject();
			pgObj.setType("json");
			pgObj.setValue(new ObjectMapper().writeValueAsString(value));
			ps.setObject(idx, pgObj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * gets value of given JDBC index from the given result
	 * set.
	 * ResultAPI access a field by type specifc method.
	 * This method {@link ResultSetMetaData#getColumnType(int)
	 * finds} SQL type. For each SQL type, calls type specific
	 * ResultSet accessor.  
	 * 
	 * @param i JDBC index (1-based)
	 * @param rs result set
	 * @return
	 * @throws SQLException
	 */
	public static Object getValue(int i, ResultSet rs) throws SQLException {
		int sqlType = rs.getMetaData().getColumnType(i);
		switch (sqlType) {
		case Types.BIGINT:
			return rs.getLong(i);
		case Types.VARCHAR:
			return rs.getString(i);
		case Types.INTEGER:
			return rs.getInt(i);
		case Types.SMALLINT:
			return rs.getShort(i);
		case Types.DOUBLE:
			return rs.getDouble(i);
		case Types.TIMESTAMP:
			return rs.getTimestamp(i);
		case Types.TIME:
			return rs.getTime(i);
		case Types.BIT:
			return rs.getBoolean(i);
		case 1111:
			return rs.getString(i);
		default:
			String name = rs.getMetaData().getColumnLabel(i);
			int type = rs.getMetaData().getColumnType(i);
			throw new RuntimeException("Column " + name
					+ " is of type " + TypeUtils.mapDatabaseColumnType(type, name)
					+ " but not supported");
		}
	}
	
	public static <T> T getValue(String label, ResultSet rs, Class<T> cls) throws SQLException {
		Object value = getValue(label, rs);
		return Conversion.convert(value, cls);
	}
	
	/**
	 * gets the value of given label.
	 * 
	 * @param label a column label
	 * @param rs result set
	 * @return null if column label does not exist
	 * @throws SQLException
	 */
	public static Object getValue(String label, ResultSet rs) throws SQLException {
		int jdbcIndex = -1;
		try {
			jdbcIndex = rs.findColumn(label);
		} catch (SQLException ex) {
			String msg = "can not find column "
					+ "[" + label + "]. available columns are "
					+ getColumnNames(rs.getMetaData());
			logger.warn(msg);
			return null;
			//throw new IllegalArgumentException(msg, ex);
		}
		Object value = getValue(jdbcIndex, rs);
		return value;
	}

	
	public static ResultSet executeQuery(Connection con, 
			SQL sql) throws SQLException {
		if (!sql.isSelect()) {
			throw new IllegalArgumentException("can not query with " + sql.getVerb() 
			+ " SQL:" + sql);
		}
		if (showSQL) logger.info("executeQuery:" + sql);
		PreparedStatement ps = con.prepareStatement(sql.toString());
		List<Object> params = sql.getBoundValues();
		
		for (int i = 0; i < params.size(); i++) {
			Object value = params.get(i);
			int jdbcIndex = i+1;
			
			setValueToPreparedStatement(ps, jdbcIndex, value);
		}
		ResultSet rs = ps.executeQuery();
		
		return rs;
	}
	/**
	 * 
	 * @param con
	 * @param sql
	 * @throws SQLException
	 */
	public static void execute(Connection con, 
			SQL sql) throws SQLException {
		if (sql.isSelect()) {
			throw new IllegalArgumentException("can not execute " + sql.getVerb() 
			+ " SQL " + sql.toString());
		}
		if (showSQL) logger.info("execute:" + sql);
		PreparedStatement ps = con.prepareStatement(sql.toString());
		List<Object> params = sql.getBoundValues();
		
		for (int i = 0; i < params.size(); i++) {
			Object value = params.get(i);
			setValueToPreparedStatement(ps, i+1, value);
		}
		try {
			ps.execute();
		} catch (Exception e) {
			throw new RuntimeException("execution of SQL " + sql + " failed"
					+ " with following exception", e);
		}
	}
	
	public static <T> List<T> extractColumn(ResultSet rs, String name, Class<T> cls) throws SQLException {
		List<T> result = new ArrayList<T>();
		while (rs.next()) {
			Object value = rs.getObject(name);
			result.add(Conversion.convert(value, cls));
		}
		return result;
	}
	
	/**
	 * sets value to PreparedStatement
	 * @param ps
	 * @param jdbcIndex
	 * @param value
	 * @throws SQLException
	 */
	private static void setValueToPreparedStatement(PreparedStatement ps, int jdbcIndex, Object value) throws SQLException {
		if (value == null) {
			// TO DO: determine SQL type
			ps.setNull(jdbcIndex, Types.VARCHAR);
		} else if (String.class.isInstance(value)) {
			ps.setString(jdbcIndex, String.class.cast(value));
		} else if (Integer.class.isInstance(value)){
			ps.setInt(jdbcIndex, Integer.class.cast(value));
		} else if (Long.class.isInstance(value)){
			ps.setLong(jdbcIndex, Long.class.cast(value));
		} else if (Boolean.class.isInstance(value)){
			ps.setBoolean(jdbcIndex, Boolean.class.cast(value));
		} else if (Time.class.isInstance(value)) {
			ps.setTime(jdbcIndex, Time.class.cast(value), Calendar.getInstance());
		} else if (Timestamp.class.isInstance(value)) {
			ps.setTimestamp(jdbcIndex, Timestamp.class.cast(value), Calendar.getInstance());
		} else if (Date.class.isInstance(value)) {
			java.sql.Date sqlDate = new java.sql.Date(Date.class.cast(value).getTime());
			ps.setDate(jdbcIndex, sqlDate);
		} else if (java.sql.Date.class.isInstance(value)) {
			ps.setDate(jdbcIndex, java.sql.Date.class.cast(value));
		} else if (JsonNode.class.isInstance(value)) {
			setJson(ps, jdbcIndex, JsonNode.class.cast(value));
		} else {
			try {
				// make a 'hail merry' pass
				ps.setObject(jdbcIndex, value.toString());
			} catch (Exception e) {
				throw new IllegalArgumentException("can not set parameter at JDBC index " 
					+ jdbcIndex + " of value type " + value.getClass(), e);
			}
		}
	}
	
	public static List<String> getColumnNames(ResultSetMetaData meta) {
		try {
			int N = meta.getColumnCount();
			List<String> names = new ArrayList<>();
			names.add("" + N + " columns:");
			for (int i = 1; i <= N; i++) {
				names.add(meta.getColumnName(i));
			}
			return names;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	


}
