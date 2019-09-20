package com.nutanix.bpg.measure.model;

import java.io.Serializable;
import java.sql.Types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.measure.utils.Identifable;

/**
 * a dimension encodes meta information
 * about a measurement value. 
 * <p>
 * A {@link Metrics} is an <em>ordered</em> set 
 * of dimensions.
 * 
 * @author pinaki.poddar
 *
 */

@SuppressWarnings("serial")
public class MetricsDimension implements Named, Identifable, Serializable {
	private String name;
	private String description  = "";
	private String alias        = "";
	private String javaTypeName = "string";
	private String sqlTypeName  = "VARCHAR";
	private int    sqlTypeInt   = Types.VARCHAR;
	private boolean numeric     = false;
	
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_DESC = "description";
	private static final String PROPERTY_JAVA_TYPE = "java-type";
	private static final String PROPERTY_SQL_TYPE  = "sql-type";
	private static final String PROPERTY_NUMERIC  = "numeric";
	
	protected MetricsDimension() {
	}
	
	public MetricsDimension(String name) {
		this();
		setName(name);
	}
	
	public MetricsDimension(JsonNode json) {
		this(json.get(PROPERTY_NAME).asText());
		setDescription(json.path(PROPERTY_DESC).asText());
		if (json.has(PROPERTY_JAVA_TYPE)) {
			setJavaTypeName(json.path(PROPERTY_JAVA_TYPE).asText());
		} 
		if (json.has(PROPERTY_SQL_TYPE)) {
			setSqlTypeName(json.path(PROPERTY_SQL_TYPE).asText());
		} 
		if (json.has(PROPERTY_NUMERIC)) {
			numeric = json.path(PROPERTY_NUMERIC).asBoolean();
		}
	}
	/**
	 * affirms if value in this dimension
	 * is numeric.
	 * 
	 * @return
	 */
	public boolean getNumeric() {
		return numeric;
	}
	
	public void setNumeric(boolean flag) {
		numeric = flag;
	}
	
	public String getId() {
		return getName();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * name of a measuring dimension
	 * @return a name, never null or empty
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("null/empty name for dimension");
		}
		this.name =  name;
	}
	
	@JsonProperty("sql-type")
	public String getSqlTypeName() {
		return sqlTypeName;
	}
	
	@JsonProperty("java-type")
	public String getJavaTypeName() {
		return javaTypeName;
	}
		
	public void setJavaTypeName(String type) {
		if (type == null) {
			throw new IllegalArgumentException("null Jave type name");
		}
		this.javaTypeName = type;
	}
	
	public void setSqlTypeName(String type) {
		if (type == null) {
			throw new IllegalArgumentException("null SQL type name");
		}
		this.sqlTypeName = type;
	}

	public String toString() {
		return "metrics dimension:" + getName();
	}
		

	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getSqlTypeInt() {
		return sqlTypeInt;
	}

	public void setSqlTypeInt(int sqlTypeInt) {
		this.sqlTypeInt = sqlTypeInt;
	}
}
