package com.nutanix.bpg.measure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.jdbc.SQL;
import com.nutanix.bpg.measure.jdbc.SQL.OP;
import com.nutanix.bpg.measure.jdbc.SQLQueryExcutor;
import com.nutanix.bpg.measure.jdbc.SelectSQL;
import com.nutanix.bpg.measure.model.DataMapping;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Measurements;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measurer.dao.MeasurementDAO;
import com.nutanix.bpg.measurer.dao.SnapshotDAO;
import com.nutanix.bpg.workload.PGBench;

/**
 * 
 * @author pinaki.poddar
 *
 */
class SnapshotController {
	private final Connection con;
	private static Logger logger = LoggerFactory.getLogger(SnapshotController.class);

	/**
	 * create with a connection to repository.
	 * 
	 * @param con
	 */
	public SnapshotController(Connection con) {
		this.con = con;
	}

	public List<Snapshot> query(SelectSQL sql, Metrics m) throws Exception {
		ResultSet rs = SQLQueryExcutor.executeQuery(con, sql);
		return createView(rs, m);
	}
	
	public Snapshot querySingle(SelectSQL sql, Metrics m) throws Exception {
		List<Snapshot> snapshots = query(sql, m);
		if (snapshots.isEmpty()) {
			throw new IllegalArgumentException("no snapshots found by SQL "
					+ sql + " with " + sql.getBoundValues() 
					+ " expected one");
		}
		return snapshots.get(0);
	}

	
	public Snapshot getSnapshotById(String id) {
		try {
			SelectSQL sql = new SelectSQL()
					.from(SnapshotDAO.SNAPSHOT_VIEW_TABLE)
					.selectAll()
					.where(SnapshotDAO.SNAPSHOT_ID, OP.EQUALS, id);
			ResultSet rs = SQLQueryExcutor.executeQuery(con, sql);
			rs.next();
			// TODO
			Metrics metrics = null;//MeasurementServerImpl.getInstance().getMetrics(metricsName);
			Snapshot sn = SnapshotDAO.newSnapshot(rs, metrics);
			return sn;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Snapshot getSnapshotByName(String name, Metrics m) {
		SelectSQL sql = new SelectSQL();
		sql.from(SnapshotDAO.SNAPSHOT_VIEW_TABLE)
		   .selectAll()
		   .where(SnapshotDAO.SNAPSHOT_NAME, SQL.OP.EQUALS, name);
		try {
			return querySingle(sql, m);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Measurements getMeasurements(
			Snapshot sn, Metrics metrics) {
//		SelectSQL sql = new SelectSQL();
//		sql.from(MeasurementDAO.MEASUREMENT_TABLE)
//		   .join(SnapshotDa)
//			.where(SnapshotDAO.SNAPSHOT_ID, SQL.OP.EQUALS, name);
		String sql = "select m.*" 
				+ " FROM " + DataMapping.getTableForMetrics(metrics)
				+ " m," + SnapshotDAO.SNAPSHOT_TABLE + " s"
				+ " WHERE m." + MeasurementDAO.MEASUREMENT_ID
				+ "=s." + SnapshotDAO.SNAPSHOT_MEASUREMENT_ID
				+ " AND s." + SnapshotDAO.SNAPSHOT_ID + "=?";
		logger.debug("executing " + sql + " with parameters " + sn.getId() + "");
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, sn.getId());
			Measurements result = new Measurements();
			ResultSet rs =  ps.executeQuery();
			logger.debug("got result columns " + SQLQueryExcutor.getColumnNames(rs.getMetaData()));
			while (rs.next()) {
				Measurement m = MeasurementDAO.newMeasurement(rs, metrics, true);
				result.addMeasurement(m);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Measurements getBenchmark(
			String name) {
//		SelectSQL sql = new SelectSQL();
//		sql.from(MeasurementDAO.MEASUREMENT_TABLE)
//		   .join(SnapshotDa)
//			.where(SnapshotDAO.SNAPSHOT_ID, SQL.OP.EQUALS, name);
		Metrics metrics = PGBench.METRICS;
		String sql = "select *" 
				+ " FROM "  + DataMapping.getTableForMetrics(metrics)
				+ " WHERE " + MeasurementDAO.MEASUREMENT_CONTEXT_TYPE + "=?"
				+ " AND "   + MeasurementDAO.MEASUREMENT_CONTEXT + "=?"
				+ " AND "   + MeasurementDAO.MEASUREMENT_METRICS + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			logger.debug("execute SQL:[" + sql + "]");
			ps.setString(1, PGBench.CONTEXT_TYPE);
			ps.setString(2, name);
			ps.setString(3, PGBench.METRICS_NAME);
			logger.debug("\t" + MeasurementDAO.MEASUREMENT_CONTEXT_TYPE + "=" + PGBench.CONTEXT_TYPE);
			logger.debug("\t" + MeasurementDAO.MEASUREMENT_CONTEXT + "=" + name);
			logger.debug("\t" + MeasurementDAO.MEASUREMENT_METRICS + "=" + PGBench.METRICS_NAME);
			Measurements result = new Measurements();
			ResultSet rs =  ps.executeQuery();
			logger.debug("got result columns " + SQLQueryExcutor.getColumnNames(rs.getMetaData()));
			while (rs.next()) {
				Measurement m = MeasurementDAO.newMeasurement(rs, metrics, true);
				m.setMetrics(PGBench.METRICS);
				result.addMeasurement(m);
			}
			logger.debug("found " + result.getSize() + " measurements");
			if (result.isEmpty()) {
				result.setMetrics(PGBench.METRICS);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}






	public List<Snapshot> getSnapshotsByTimeRange(
			Metrics metrics,
			Date start, Date end) {
		SelectSQL sql = new SelectSQL()
		   .from(SnapshotDAO.SNAPSHOT_VIEW_TABLE)
		   .selectAll()
		   .where(SnapshotDAO.SNAPSHOT_METRICS, SQL.OP.EQUALS, metrics.getName());
		
		if (start != null) {
			sql.where(SnapshotDAO.SNAPSHOT_MEASUREMENT_START_TIME, SQL.OP.GREATER_OR_EQUAL, start.getTime());
		}
		if (end != null) {
			sql.where(SnapshotDAO.SNAPSHOT_MEASUREMENT_END_TIME, SQL.OP.LESS_OR_EQUAL, end.getTime());
		}
		sql.groupBy(SnapshotDAO.SNAPSHOT_ID);
		
		logger.info(sql.toString());
		try {
			ResultSet rs = SQLQueryExcutor.executeQuery(con, sql);
			return createView(rs, metrics);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

//	public Future<Snapshot> takeSnapshot(MeasurementServer ctx, 
//			String name, 
//			Metrics metrics, Database database, 
//			SnapshotSchedule schedule) throws Exception {
//		SnapshotPlugin plugin = new SnapshotPlugin();
//		Snapshot snapshot = new Snapshot().setMetrics(metrics);
//		snapshot.setStartTime(System.currentTimeMillis());
//		String snapshotId = UUID.randomUUID().toString();
//		snapshot.setId(snapshotId);
//		snapshot.setName(name);
//		snapshot.setExpectedMeasurementCount(schedule.getCount());
//		Map<String, Object> args = new HashMap<String, Object>();
//		args.put(SnapshotPlugin.PARAM_SNAPSHOT, snapshot);
//		args.put(SnapshotPlugin.PARAM_METRICS,  metrics);
//		args.put(SnapshotPlugin.PARAM_DATABASE, database);
//		args.put(SnapshotPlugin.PARAM_SCHEDULE, schedule);
//		plugin.run(args, con);
//		
//		return true;
//	}

	private List<Snapshot> createView(ResultSet rs, Metrics m) {
		List<Snapshot> views = new ArrayList<Snapshot>();
		logger.info("populating view from result set");
		try {
			while (rs.next()) {
				views.add(SnapshotDAO.newSnapshot(rs, m));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		logger.info("populated " + views.size() + " items");
		return views;
	}
}
