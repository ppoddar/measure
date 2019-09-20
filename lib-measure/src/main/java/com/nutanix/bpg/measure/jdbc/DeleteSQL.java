package com.nutanix.bpg.measure.jdbc;

import com.nutanix.bpg.model.MetricsDimension;

public class DeleteSQL extends SQL {
	
	public DeleteSQL() {
		super(SQL.VERB.DELETE);
	}

	/**
	 * add given metrics with given alias.
	 * @param m
	 * @param alias
	 * @return
	 */
	public DeleteSQL from(String t, String a) {
		tableAliases.add(new TableAlias(t, a));
		return this;
	}
	
	public DeleteSQL from(String t) {
		return from(t, null);
	}
	
	public DeleteSQL where(MetricsDimension dim, SQL.OP op, Object value) {
		super.where(dim, op, value);
		return this;
	}
	public DeleteSQL where(String dim, SQL.OP op, Object value) {
		super.where(dim, op, value);
		return this;
	}
	
	public String toString() {
		String sql = verb.toString()
			+ " FROM " + concatenate(COMMA, tableAliases);
		if (!whereClauses.isEmpty()) {
			sql += " WHERE " + concatenate(AND, whereClauses);  
		}
		
		return sql;
	}

	
	

	

}
