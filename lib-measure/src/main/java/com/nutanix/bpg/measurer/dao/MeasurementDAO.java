package com.nutanix.bpg.measurer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.jdbc.DynamicTable;
import com.nutanix.bpg.measure.jdbc.InsertSQL;
import com.nutanix.bpg.measure.jdbc.SQLQueryExcutor;
import com.nutanix.bpg.measure.model.DataMapping;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.model.MetricsDimension;
import com.nutanix.bpg.utils.StringUtils;

/**
 * Manages persistent CRUD operations for  {@link Measurement 
 * measurement}
 * 
 * @author pinaki.poddar
 *
 */
public class MeasurementDAO {
	public static String ID         = "id"; 
	public static String START_TIME = "start_time"; 
	public static String END_TIME   = "end_time"; 
	public static String CONTEXT_TYPE  = "ctx_type"; 
	public static String CONTEXT    = "ctx"; 
	public static String METRICS    = "metrics"; 
	
	private static String[] CONTEXT_COLUMNS = {
			ID,
			START_TIME,
			END_TIME,
			CONTEXT_TYPE,
			CONTEXT,
			METRICS
	};
	
	private static Logger logger = LoggerFactory.getLogger(MeasurementDAO.class);
	

	/**
	 * measurement are stored in a <em>dynamic</em> 
	 * table whose name is 
	 * derived from given Metrics' name.
	 * The column name and type of this dynamic table 
	 * are described in {@link MetricsDimension}
	 * of a {@link Metrics}.
	 * 
	 * @param con a connection
	 * @param metrics a metrics
	 */
	public MeasurementDAO(Connection con, Metrics metrics) 
		throws SQLException {
		DynamicTable table = new DynamicTable(DataMapping.getTableForMetrics(metrics), metrics);
		table.create(con, metrics);
	}
	
	
	/**
	 * inserts a {@link Measurement} vector 
	 * alongwith contextual data to a dynamic table
	 * 
	 * @param m a measurement that must have its 
	 * context and data set up.
	 */
	public void insert(Connection con, Measurement m) {
		try {
			validateContext(m);
			InsertSQL sql = new InsertSQL();
			sql.into(DataMapping.getTableForMetrics(m.getMetrics()));
			sql.insert(ID,         m.getId());
			sql.insert(METRICS,    m.getMetrics().getName());
			sql.insert(START_TIME, m.getStartTime());
			sql.insert(END_TIME,   m.getEndTime());
			sql.insert(CONTEXT_TYPE, m.getContextType());
			sql.insert(CONTEXT,    m.getContext());
			for (MetricsDimension dim : m.getMetrics()) {
				sql.insert(dim, m.getValue(dim.getName()));
			}
			logger.debug("insert measurement: " + m.getStartTime() 
					+ " SQL:" + sql);
			SQLQueryExcutor.execute(con, sql);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static void validateContext(Measurement m) {
		if (StringUtils.isEmpty(m.getId())) {
			throw new IllegalStateException("id is not set");
		}
		if (StringUtils.isEmpty(m.getContext())) {
			throw new IllegalStateException("context is not set");
		}
		if (StringUtils.isEmpty(m.getContextType())) {
			throw new IllegalStateException("context type is not set");
		}
		if (m.getMetrics() == null) {
			throw new IllegalStateException("metrics is not set");
		}
		if (m.getStartTime() <= 0) {
			throw new IllegalStateException("start time is not set");
		}
		if (m.getEndTime() <= 0) {
			throw new IllegalStateException("end time is not set");
		}
	}
	
	private static void validateResultSet(ResultSet rs, Metrics metrics,
			boolean hasContextColumns) throws SQLException {
		int actual = rs.getMetaData().getColumnCount();
		int N = metrics.getDimensionCount();
		int expected = N;
		if (hasContextColumns) {
			expected = N + CONTEXT_COLUMNS.length;
			if (actual != expected) {
				throw new IllegalArgumentException("expected " + expected
					+ " columns in resultset. " + N + " for measurement values"
					+ " and " + CONTEXT_COLUMNS + " for contexts"
					+ " but given result set has " + actual + " columns");
			}
		} else {
			if (actual != expected) {
				throw new IllegalArgumentException("expected " + expected
					+ " columns in resultset. " + N + " for measurement values"
					+ " but given result set has " + actual + " columns");
			}
		}
	}
	/**
	 * creating a measurement from a row in measurement 
	 * table.
	 * Note: Measurement table row has columns other
	 * that dimension values. 
	 * The JDBC index must account for those context columns.
	 * @param rs an entire row of measurement table.
	 * @param metrics
	 * @return
	 * @throws SQLException
	 */
	public static Measurement newMeasurement(
			ResultSet rs, Metrics metrics,
			boolean hasContextColumns) throws SQLException {
		try {
			Measurement m = new Measurement(metrics);
			validateResultSet(rs, metrics, hasContextColumns);
			int N = metrics.getDimensionCount();
			int K = hasContextColumns ? CONTEXT_COLUMNS.length : 0;
			for (int i = 0; i < N; i++) {
				int jdbcIndex = i+1+K;
				Object value = SQLQueryExcutor.getValue(jdbcIndex, rs);
				m.putValue(metrics.getDimension(i), value);
			}
			if (hasContextColumns) {
				m.setId(rs.getString(ID));
				m.setStartTime(rs.getLong(START_TIME));
				m.setEndTime(rs.getLong(END_TIME));
				m.setContext(
					rs.getString(CONTEXT_TYPE),
					rs.getString(CONTEXT));
			}
			return m;
		} catch (Exception e) {
			throw new RuntimeException("can not create measurmemnt "
						+ " from result set columns " 
						+ SQLQueryExcutor.getColumnNames(rs.getMetaData())
						+ " due to ", e);
		}
	}
	

	
	/**
	 * Get the values in each dimension.
	 * @param con
	 * @param ctx
	 * @param metrics
	 * @param dims
	 * @return
	 * @throws SQLException
	 */
//	public static Map<String, List<Double>> getTrend(
//			Connection con,
//			String ctx, 
//			Metrics metrics, String[] dims) throws SQLException {
//		Map<String, List<Double>> result = new HashMap<String, List<Double>>();
//		SelectSQL sql = new SelectSQL();
//		String tableName = DataMapping.getTableForMetrics(metrics);
//		sql.from(tableName);
//		for (String d : dims) {
//			result.put(d, new ArrayList<Double>());
//			sql.select(d);
//		}
//		sql.where(CONTEXT, SQL.OP.EQUALS, ctx);
//		sql.where(METRICS, SQL.OP.EQUALS, metrics.getName());
//		ResultSet rs = SQLQueryExcutor.executeQuery(con, sql);
//		while (rs.next()) {
//			for (String d: dims) {
//				Double v = SQLQueryExcutor.getValue(d, rs, Double.class);
//				result.get(d).add(v);
//			}
//		}
//		return result;
//	}
}
