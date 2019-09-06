package com.nutanix.bpg.measure.jdbc;

import com.nutanix.bpg.measure.model.DataMapping;
import com.nutanix.bpg.measure.model.MetricsDimension;
/**
 * an SQL statement for insert
 * 
 * @author pinaki.poddar
 *
 */
public class UpdateSQL extends SQL {

	public UpdateSQL() {
		super(SQL.VERB.UPDATE);
	}
	
	public String toString() {
		String sql = verb + SPACE;
		sql += SPACE + tableAliases.get(0).toString()
			+ " SET ";
		
		for (int i = 0; i< projections.size(); i++) {
			Projection p = projections.get(i);
			MetricsDimension dim = p.getDimension();
			sql += SPACE + dim.getName() + EQUALS + BIND_MARKER;
			
			if (i < projections.size()-1) {
				sql += COMMA;
			}
			sql += getWhere();
		};
		return sql;
	}
	
	
	
	/**
	 * set table name to update
	 * @param m
	 * @return
	 */
	public UpdateSQL update(String t) {
		if (!tableAliases.isEmpty()) {
			throw new IllegalStateException("can not update " + t
					+ " one table already is updated");
		}
		tableAliases.add(new TableAlias(t, null));
		return this;
	}
	
	/**
	 * set values 
	 * @param m
	 * @return
	 */
	
	public UpdateSQL set(MetricsDimension dim, Object value) {
		project(dim, null);
		bind(dim, value);
		return this;
	}
	
	public UpdateSQL set(String name, Object value) {
		MetricsDimension dim = DataMapping.getMappedDimension(name);
		return set(dim, value);
	}
	public UpdateSQL where(String dim, SQL.OP op, Object value) {
		super.where(dim, op, value);
		return this;
	}
	


}
