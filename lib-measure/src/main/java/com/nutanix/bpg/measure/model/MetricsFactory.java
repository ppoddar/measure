package com.nutanix.bpg.measure.model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.measure.utils.JsonUtils;

public class MetricsFactory implements Factory<Metrics> {
	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private static final Logger logger = LoggerFactory.getLogger(MetricsFactory.class);
	
	@Override
	public Class<Metrics> getType() {
		return Metrics.class;
	}
	
	private static final String PROPERTY_NAME        = "name";
	private static final String PROPERTY_DESCRIPTION = "description";
	private static final String PROPERTY_DIMENSIONS  = "dimensions";
	
	public Metrics build(InputStream in) throws Exception {
		JsonNode json = mapper.readValue(in, JsonNode.class);
		JsonUtils.assertProperty(json, PROPERTY_NAME);
		Metrics metrics = new Metrics(json.get(PROPERTY_NAME).asText());
		metrics.setDescription(json.path(PROPERTY_DESCRIPTION).asText());
		JsonUtils.assertProperty(json, PROPERTY_DIMENSIONS);
		JsonNode dimensionNodes = json.get(PROPERTY_DIMENSIONS);
		for (JsonNode dimNode : dimensionNodes) {
			MetricsDimension dim = mapper.convertValue(dimNode, MetricsDimension.class);
			metrics.addDimension(dim);
		}
		
		return metrics;
	}
	
	
	public static Catalog<Metrics> build(Database db) {
		Catalog<Metrics> metricsCatalog = new Catalog<Metrics>();
		try {
			logger.debug("creating metrics catalog");
			Connection con = db.getConnection();
			DatabaseMetaData meta = con.getMetaData();
			ResultSet rs = meta.getTables(
					db.getCatalog(), db.getSchema(), 
					db.getTableNamePattern(), db.getStatisticsTableTypes());
			while (rs.next()) {
				String tableName = rs.getString(TABLE_NAME);
				if (!db.isStatisticsTable(tableName)) continue;
				String metricsName = tableName;
				Metrics metrics = new Metrics(metricsName);
				logger.trace("adding " + metrics);
				metricsCatalog.add(metrics);
				ResultSet columns = meta.getColumns(db.getCatalog(), db.getSchema(), tableName, null);
				while (columns.next()) {
					MetricsDimension dim = createDimension(columns);
					metrics.addDimension(dim);
				}
				if (metrics.getDimensionCount() == 0) {
					throw new RuntimeException("no column in " 
							+ getFullName(db,tableName));
				}
			}
			if (metricsCatalog.isEmpty()) {
				throw new RuntimeException("no table in " + db
						+ " matching catalog=" + db.getCatalog()
						+ " schema=" + db.getSchema()
						+ " table name pattern=" + db.getTableNamePattern());
			} else {
				logger.debug("created " + metricsCatalog.size() + " metrics  for " + db);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return metricsCatalog;
	}
	
	public static String getFullName(Database db, String tableName) {
		String full = "";
		if (db.getCatalog() != null) 
			full = db.getCatalog() + ":";
		if (db.getSchema() != null) full += db.getSchema() + ":";
		full += tableName;
		
		return full;
	}
	
	private static final String TABLE_NAME  = "TABLE_NAME";
	private static final String  REMARKS  = "REMARKS";
	private static final String  COLUMN_NAME  = "COLUMN_NAME";
	private static final String  DATA_TYPE  = "DATA_TYPE";
	private static final String  TYPE_NAME  = "TYPE_NAME";

	public static MetricsDimension createDimension(
			ResultSet rs) throws SQLException {
		MetricsDimension dim = new MetricsDimension(
				rs.getString(COLUMN_NAME));
		dim.setSqlTypeName(rs.getString(TYPE_NAME));
		dim.setSqlTypeInt(rs.getInt(DATA_TYPE));
		dim.setDescription(rs.getString(REMARKS));
		dim.setNumeric(isNumeric(dim));
		logger.trace("dim " + dim.getName() 
		+ " sql:" + dim.getSqlTypeInt() + ":" + dim.getSqlTypeName()
		+ " numeric:" + dim.getNumeric());
		return dim;
	}
	
	private static List<Integer> NUMERIC_SQL_TYPES = 
			Arrays.asList(
					Types.DECIMAL, 
					Types.DOUBLE, Types.FLOAT,
					Types.INTEGER, Types.BIGINT,
					Types.SMALLINT);
	private static List<String> NON_NUMERIC_FIELD_NAMES = 
			Arrays.asList(
					"datid", "pid", "usesysid", "client_port",
					"relid", "indexrelid","idx_scan");
	
	private static boolean isNumeric(MetricsDimension dim) {
		return NUMERIC_SQL_TYPES.contains(dim.getSqlTypeInt())
		   && !NON_NUMERIC_FIELD_NAMES.contains(dim.getName()); 
	
	}
}
