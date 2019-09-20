package com.nutanix.bpg.measure.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasuremementTaker;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.sql.SQL;
import com.nutanix.bpg.sql.SQLQueryExcutor;
import com.nutanix.bpg.sql.SelectSQL;

/**
 * takes a measurement on a database table.
 *  
 * @author pinaki.poddar
 *
 */
public class JDBCMeasurmentTaker implements MeasuremementTaker {
	private final Metrics metrics;
	private final Database db;
	public static String DATABASE_NAME = "datname"; 
	private static final Logger logger = LoggerFactory.getLogger(JDBCMeasurmentTaker.class);
	
	public JDBCMeasurmentTaker(Database db, Metrics m) {
		this.db      = db;
		this.metrics = m;
	}
	/**
	 * read from target database.
	 * The target database has collected statistics
	 * in tables. This method fetches the table values,
	 * it is called a Measurement vector.
	 * <p>
	 * Creates a similar table in private repository,
	 * where the columns have name and type. The name
	 * of the table is similar to table in target database.
	 * The table also has columns to save context about
	 * the measurement.
	 * <p>
	 * Then contextual data is added to measurement vector
	 * and stored. 
	 * <p>
	 * A measurement is considered to be part of a {@link
	 * Snapshot}.
	 * 
	 * @param db target database
	 * @param metrics column definitions in 
	 * statistics collection table 
	 * @param sn Snapshot to which a measurement would be
	 * added. 
	 * @param con connection to repository to store 
	 * snapshot measurement
	 * 
	 * @throws Exception
	 */
	public Measurement takeMeasurement()  throws Exception  {
		// query is executed on target database
		// NOTE: Target table name is same as metrics name.
		// Fetch all columns of target table.
		// statistics collection table has
		// DATABASE_NAME column that identifies 
		// target database
		SelectSQL sql = new SelectSQL()
				   .from(metrics.getName())
				   .selectAll()
				   .where(metrics.getDimension(DATABASE_NAME), 
						   SQL.OP.EQUALS, db.getName());
		
		logger.debug("fetch statistics from target table by SQL:" + sql);
		Measurement m = new Measurement(metrics);
		m.setId(UUID.randomUUID().toString());
		Connection targetCon = null;
		try {
			targetCon = db.getConnection();
			m.setStartTime(System.currentTimeMillis());
			ResultSet rs = SQLQueryExcutor.executeQuery(targetCon, sql);
			m.setEndTime(System.currentTimeMillis());
			if (!rs.next()) {
				throw new RuntimeException("can not get measurement from target "
						+ db + " with SQL " + sql);
			}
			int N = rs.getMetaData().getColumnCount();
			int M = metrics.getDimensionCount();
			if (N != M) {
				throw new RuntimeException("result from target database has "
						+ N + " columns" + " but " + metrics + " has " + M
						+ " has dimensions");
			} 
			for (int i = 0; i < N; i++) {
				Object value = SQLQueryExcutor.getValue(i+1, rs);
				m.putValue(metrics.getDimension(i), value);
			}
			return m;
		} finally {
			closeConnection(targetCon); 
		}
	}
	
	private void closeConnection(Connection con) {
		 if (con != null) {
			 try {
				 con.close();
			 } catch (Exception ex) {
				 // ignore
			 }
		 }
	}
}
