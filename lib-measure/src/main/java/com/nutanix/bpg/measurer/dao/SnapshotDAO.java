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
import com.nutanix.bpg.measure.model.Snapshot;

public class SnapshotDAO  {
	public static final String SNAPSHOT_TABLE       = "SNAPSHOTS";
	public static final String SNAPSHOT_VIEW_TABLE  = "SNAPSHOT_VIEWS";
	public static final String ID          = "id";
	public static final String NAME        = "name";
	public static final String EXPECTED_COUNT    = "expected_count";

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
		sql.insert(ID,             sn.getId());
		sql.insert(NAME,           sn.getName());
		sql.insert(EXPECTED_COUNT, sn.getExpectedMeasurementCount());
		
		con.setAutoCommit(false);
		SQLQueryExcutor.execute(con, sql);
		con.commit();
	}
	
	public List<Snapshot> query(Connection con, SQL sql, Object ctx) throws SQLException {
		ResultSet rs = SQLQueryExcutor.executeQuery(con, sql);
		List<Snapshot> snapshots = new ArrayList<>();
		while (rs.next()) {
			Snapshot snapshot = newSnapshot(rs);
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
	public static Snapshot newSnapshot(ResultSet rs) throws SQLException {
		Snapshot snapshot = new Snapshot();
		populate(snapshot, rs);
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
//	public static Snapshot newSnapshot(ResultSet rs, Metrics m) throws SQLException {
//		Snapshot snapshot = new Snapshot();
//		String metricsName = SQLQueryExcutor.getValue(SNAPSHOT_METRICS, rs, String.class);
//		if (!metricsName.equals(m.getName())) {
//			throw new IllegalStateException("database metrics " + metricsName
//					+ " given " + m);
//		}
//		populate(snapshot, m, rs);
//		return snapshot;
//	}
//	
	private static void populate(Snapshot snapshot, ResultSet rs) throws SQLException {
		snapshot.setId(SQLQueryExcutor.getValue(ID, rs, String.class));
		snapshot.setName(SQLQueryExcutor.getValue(NAME, rs, String.class));
		snapshot.setExpectedMeasurementCount(SQLQueryExcutor.getValue(EXPECTED_COUNT, rs, Integer.class));
	}
}
