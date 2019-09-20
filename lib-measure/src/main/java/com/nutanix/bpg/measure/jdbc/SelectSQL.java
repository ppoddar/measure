package com.nutanix.bpg.measure.jdbc;

import com.nutanix.bpg.measure.model.DataMapping;
import com.nutanix.bpg.model.MetricsDimension;

public class SelectSQL extends SQL {
	private boolean PROJECT_WILDCARD;
	
	public SelectSQL() {
		super(SQL.VERB.SELECT);
	}
	
	public boolean isSelect() {
		return true;
	}
	
	public SelectSQL groupBy(MetricsDimension dim) {
		return this;
	}
	public SelectSQL groupBy(String name) {
		return this;
	}

	/**
	 * add given metrics with given alias.
	 * @param m
	 * @param alias
	 * @return
	 */
	public SQL from(String t, String a) {
		tableAliases.add(new TableAlias(t, a));
		return this;
	}
	
	public SelectSQL from(String m) {
		tableAliases.add(new TableAlias(m, null));
		return this;
	}
	
	public SelectSQL selectAll() {
		PROJECT_WILDCARD = true;
		return this;
	}
	
	public SelectSQL select(String name, String alias) {
		project(DataMapping.getMappedDimension(name), alias);
		return this;
	}
	
	public SelectSQL select(String name) {
		return select(name, null);
	}

	
	public SelectSQL where(MetricsDimension dim, SQL.OP op, Object value) {
		super.where(dim, op, value);
		return this;
	}
	public SelectSQL where(String dim, SQL.OP op, Object value) {
		super.where(dim, op, value);
		return this;
	}
	
	public String toString(boolean withParams) {
		String sql = verb.toString()
			+ SPACE    
			+ (PROJECT_WILDCARD ? "*" : concatenate(COMMA, projections))
			+ " FROM " + concatenate(COMMA, tableAliases);
		if (!whereClauses.isEmpty()) {
			sql += " WHERE " + concatenate(AND, whereClauses);  
		}
		return sql;
	}
	
	

	
	

	

}
