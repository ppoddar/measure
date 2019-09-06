package com.nutanix.bpg.measurer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.jdbc.InsertSQL;
import com.nutanix.bpg.measure.jdbc.SQL;
import com.nutanix.bpg.measure.jdbc.SQLQueryExcutor;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Resolver;
import com.nutanix.bpg.measure.model.Snapshot;

public class SnapshotDAO  {
	// These constants must match database schema definition
	// TODO: validate programmatically
	public static final String SNAPSHOT_TABLE       = "SNAPSHOTS";
	public static final String SNAPSHOT_VIEW_TABLE  = "SNAPSHOT_VIEWS";
	public static final String SNAPSHOT_ID          = "id";
	public static final String SNAPSHOT_METRICS     = "metrics";
	public static final String SNAPSHOT_NAME        = "name";
	public static final String SNAPSHOT_MEASUREMENT_ID = "measurement_id";
	public static final String SNAPSHOT_MEASUREMENT_START_TIME  = "start_time";
	public static final String SNAPSHOT_MEASUREMENT_END_TIME    = "end_time";
	public static final String SNAPSHOT_ACTUAL_COUNT      = "actual_count";
	public static final String SNAPSHOT_EXPECTED_COUNT    = "expected_count";
	public static final String SNAPSHOT_MEASUREMENT_COUNT = "count";

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(SnapshotDAO.class);
	
	
	/**
	 * Inserts given snapshot measurement in repository.
	 * @param sn snapshot
	 * @param m measurement
	 * @exception SQLException if database error
	 */
	public void insert(Connection con, Snapshot sn) throws SQLException {
		InsertSQL sql = new InsertSQL();
		sql.into(SNAPSHOT_TABLE);
		sql.insert(SNAPSHOT_ID,             sn.getId());
		sql.insert(SNAPSHOT_NAME,           sn.getName());
		sql.insert(SNAPSHOT_METRICS,        sn.getMetrics().getName());
		
//		sql.insert(SNAPSHOT_MEASUREMENT_ID, m.getId());
		sql.insert(SNAPSHOT_MEASUREMENT_START_TIME,  sn.getMeasurements().getStartTime());
		sql.insert(SNAPSHOT_MEASUREMENT_END_TIME,    sn.getMeasurements().getEndTime());
		sql.insert(SNAPSHOT_ACTUAL_COUNT,            sn.getMeasurements().getSize());
		sql.insert(SNAPSHOT_EXPECTED_COUNT,          sn.getExpectedMeasurementCount());
		
		con.setAutoCommit(false);
		SQLQueryExcutor.execute(con, sql);
		MeasurementDAO dao = new MeasurementDAO(con, sn.getMetrics());
		for (Measurement m : sn.getMeasurements()) {
			dao.insert(con, m);
		}
		con.commit();
	}
	
	public List<Snapshot> query(Connection con, SQL sql, Object ctx) throws SQLException {
		ResultSet rs = SQLQueryExcutor.executeQuery(con, sql);
		List<Snapshot> snapshots = new ArrayList<>();
		while (rs.next()) {
			Snapshot snapshot = null;//newSnapshot(rs, resolver);
			snapshots.add(snapshot);
		}
		return snapshots;
		
	}
	
	/**
	 * Creates and populates a Snapshot from given
	 * result set.
	 * @param rs a row of data
	 * @param resolver resolves metrics name to a object
	 * reference. Typically the server API.
	 * @return
	 * @throws SQLException
	 */
	public static Snapshot newSnapshot(ResultSet rs, Resolver resolver) throws SQLException {
		Snapshot snapshot = new Snapshot();
		String metricsName = SQLQueryExcutor.getValue(SNAPSHOT_METRICS, rs, String.class);
		Metrics metrics = resolver.resolve(metricsName, Metrics.class);
		populate(snapshot, metrics, rs);
		return snapshot;
	}
	
	/**
	 * create a snapshot. verifies that the metrics name
	 * in database is same as the given metrics
	 * @param rs a row of data
	 * @param m a metrics
	 * @return a snapshot
	 * @throws SQLException
	 */
	public static Snapshot newSnapshot(ResultSet rs, Metrics m) throws SQLException {
		Snapshot snapshot = new Snapshot();
		String metricsName = SQLQueryExcutor.getValue(SNAPSHOT_METRICS, rs, String.class);
		if (!metricsName.equals(m.getName())) {
			throw new IllegalStateException("database metrics " + metricsName
					+ " given " + m);
		}
		populate(snapshot, m, rs);
		return snapshot;
	}
	
	private static void populate(Snapshot snapshot, Metrics m, ResultSet rs) throws SQLException {
		snapshot.setMetrics(m);
		snapshot.setId(SQLQueryExcutor.getValue(SNAPSHOT_ID,     rs, String.class));
		snapshot.setName(SQLQueryExcutor.getValue(SNAPSHOT_NAME, rs, String.class));
		}

}
