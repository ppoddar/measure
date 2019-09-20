package com.nutanix.bpg.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.model.MetricsDimension;
import com.nutanix.bpg.utils.TypeUtils;
/**
 * Creates SQL step-by-step. 
 * SQL provides invisible, general purpose methods.
 * <br>
 * The concrete derivations such {@link SelectSQL}
 * or {@link InsertSQL} provides appropriate 
 * semantic operations.
 * 
 * @author pinaki.poddar
 *
 */
public abstract class SQL {
	public enum VERB  {SELECT, INSERT, UPDATE, DELETE};
	
	protected final VERB verb;
	protected final List<TableAlias> tableAliases;
	protected final List<Projection> projections;
	protected final List<Predicate> whereClauses;
	protected final List<Binder> binders;
	protected final List<Join> joins;
	protected final List<GroupBy> groupBys;
	
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String AS    = " AS ";
	public static final String AND   = " AND ";
	public static final String DOT   = ".";
	public static final String DESC   = "DESC";
	public static final String EQUALS   = "=";
	public static final String OPEN_BRACKET   = "(";
	public static final String CLOSE_BRACKET   = ")";
	public static final String BIND_MARKER = "?";
	public static final String CAST_MARKER = "::";

	protected Logger logger = LoggerFactory.getLogger(SQL.class);
	/**
	 * Create a SQL of given verb.
	 * @param verb
	 */
	public SQL(VERB verb) {
		this.verb = verb;
		tableAliases  = new ArrayList<>();
		projections = new ArrayList<>();
		whereClauses = new ArrayList<>();
		binders = new ArrayList<>();
		joins = new ArrayList<>();
		groupBys = new ArrayList<>();
		
	}
	
	public final VERB getVerb() {
		return verb;
	}
	
	public boolean isSelect() {
		return false;
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
	
	protected SQL where(MetricsDimension dim, OP op, Object value) {
		whereClauses.add(new Predicate(dim, op, value));
		binders.add(new Binder(dim, value));
		return this;
	}
	
	
	protected SQL projectAll(Metrics m, String alias) {
		for (MetricsDimension dim : m) {
			project(dim, null);
		}
		return this;
	}
	
	/**
	 * Adds a projection. A projection appears in 
	 * <code>SELECT</code>, <code>INSERT</code> or
	 * <code>UPDATE</code> such as 
	 * <code>SELECT id,name from T1</code>
	 * 
	 * @param name name to be projected. It can be
	 * name of a column, or a table qualified name
	 * such as t1.col
	 * @param alias can be null. 
	 * 
	 * @return same receiver
	 */
	protected SQL project(MetricsDimension dim, String alias) {
		Projection proj = new Projection(dim, alias);
		projections.add(proj);
		return this;
	}
	
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean withParams) {
		String sql = verb.toString()
			+ SPACE + concatenate(COMMA, projections) 
			+ " FROM " + concatenate(COMMA, tableAliases);
		sql += getWhere();
		
		if (withParams && !binders.isEmpty()) {
			sql += SPACE + OPEN_BRACKET;
			for (int i = 0; i < binders.size(); i++) {
				Binder b = binders.get(i);
				sql += b.getDimension().getName()
					+ EQUALS + b.getBoundValue();
				if (i != binders.size()-1)
					sql += COMMA;
			}
		}
		if (!groupBys.isEmpty()) {
			sql += " GROUP BY" + concatenate(COMMA, groupBys);
		}

		return sql;
	}
	
	protected String getWhere() {
		if (!whereClauses.isEmpty()) {
			return " WHERE " + concatenate(AND, whereClauses);  
		} else {
			return "";
		}
	}
	
	/**
	 * A list of values to be bound to this query.
	 * <br>
	 * <b>NOTE:</b>
	 * The order of values and their type are important.
	 * 
	 * @return a list of values t be bound
	 */
	public List<Object> getBoundValues() {
		List<Object> boundValues = new ArrayList<>();
		binders.forEach((b) -> boundValues.add(b.value));
		return boundValues;
	}
	
	public SQL bind(MetricsDimension dim, Object value) {
		binders.add(new Binder(dim, value));
		return this;
	}
	
	/**
	 * Operation that appear in a WHERE clause
	 *
	 */
	public enum OP  {
		EQUALS("="), 
		NOT_EQUALS("!="),
		GREATER (">"),
		GREATER_OR_EQUAL (">="),
		LESS ("<"),
		LESS_OR_EQUAL ("<="),
		IS_NULL("IS NULL"),
		IN("IN")
		;
		private final String symbol;
		private OP(String s) {
			this.symbol = s;
		}
		public String toString() {
			return symbol;
		}
	};
	
	public String concatenate(String sep, Iterable<?> elements) {
		String s = "";
		Iterator<?> i = elements.iterator();
		while (i.hasNext()) {
			Object e = i.next();
			s += e.toString();
			if (i.hasNext()) s += sep;
		}
		return s;
	}
	
	/**
	 * Binder associates a {@link MetricsDimension} 
	 * to a value.
	 *
	 */
	class Binder {
		final Object value;
		final MetricsDimension dim;
		final boolean needsCast;
		Binder(MetricsDimension d, Object v) {
			Objects.requireNonNull(d, "can not bind to null dimension");
			
			needsCast = !TypeUtils.isCompatiable(d.getSqlTypeName(), v);
			dim = d;
			value = v;
//			if (needsCast) {
//				logger.debug("value " + value + " requires cast " 
//						+ " to " + dim + " with sql type " + dim.getSqlTypeName());
//			}
		}
		
		public MetricsDimension getDimension() {
			return dim;
		}
		
		public Object getBoundValue() {
			return value;
		}
		/**
		 * 
		 */
		public String toString() {
			String s = BIND_MARKER ;
			if (needsCast) {
				s +=  CAST_MARKER + dim.getSqlTypeName();
			}
			
			return s;
		}
	}
	
	/**
	 * a projection appears in select or insert query.
	 * @author pinaki.poddar
	 *
	 */
	class Projection {
		final MetricsDimension dim;
		final String name;
		final String alias;
		
		Projection(String name, String a) {
			dim = null;
			this.name = name;
			this.alias = a;
		}
		
		Projection(MetricsDimension dim, String a) {
			this.dim = dim;
			this.name = dim.getName();
			this.alias = a;
		}

		
		public String getName() {
			return name;
		}
		public MetricsDimension getDimension() {
			if (dim == null) {
				throw new IllegalStateException("project " + this 
						+ " was not constructured from a metric dimension");
			}
			return dim;
		}
		
		public String getAlias() {
			return alias;
		}
		
		public String toString() {
			if (alias == null) {
				return name;
			}
			return name + AS + alias;
		}

	}
	
	
	/**
	 * a where condition
	 * @author pinaki.poddar
	 *
	 */
	
	class Predicate {
		final String lhs;
		final OP op;
		final Object value;
		Predicate(String name, OP op, Object value) {
			this.lhs = name;
			this.op  = op;
			this.value = value;
		}
		Predicate(MetricsDimension dim, OP op, Object value) {
			this(dim.getName(), op, value);
		}
		
		public String toString() {
			String s = lhs + op + BIND_MARKER;
			return s;
		}
	}
	
	class Join {
		
	}
	
	class TableAlias {
		final String table;
		final String alias;
		
		TableAlias(String t, String a) {
			this.table = t;
			this.alias = a;
		}
		
		public String toString() {
			String s =  table;
			if (alias == null) {
				return s;
			}
			return s + SPACE + alias;
		}
	}
	
	public class GroupBy {
		final String name;
		final boolean asc;
		public GroupBy(String name, boolean asc) {
			this.name = name;
			this.asc = asc;
		}
		public String toString() {
			return name + (asc ? "" : SPACE + DESC);
		}
	}

}
