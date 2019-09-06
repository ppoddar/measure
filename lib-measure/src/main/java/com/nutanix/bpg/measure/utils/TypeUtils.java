package com.nutanix.bpg.measure.utils;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.jdbc.SQLQueryExcutor;

public class TypeUtils {
	private static Logger logger = LoggerFactory.getLogger(SQLQueryExcutor.class);
	private static String[] NUMERIC_TYPE_NAMES = {
			"bigint", "double"};
	private static Map<String, Class<?>> typeCatalog = new HashMap<>();
	static List<Class<?>> NUMERIC_TYPES = Arrays.asList(
			int.class, Integer.class, 
			short.class, Short.class, 
			long.class, Long.class, 
			float.class, Float.class, 
			double.class, Double.class);
	
	private static Map<String, List<Class<?>>> compatiableTypes =
			new HashMap<>();
	static {
		compatiableTypes.put("VARCHAR", Arrays.asList(String.class));
		compatiableTypes.put("bool", Arrays.asList(Boolean.class, boolean.class));
		compatiableTypes.put("bigint", Arrays.asList(Long.class, long.class,
				Integer.class, int.class));
		
	}
	private static Map<Integer, String> mappings = new HashMap<Integer, String>();
	private static Map<String, String> SPECIAL_COLUMNS = new HashMap<String, String>();
	static {
		for (Field field : Types.class.getFields()) {
			try {
				mappings.put((Integer) field.get(null), field.getName());
			} catch (Exception ex) {
				logger.warn(ex.getMessage());
			}
			SPECIAL_COLUMNS.put("client_addr", "INET");
			SPECIAL_COLUMNS.put("backend_xid", "INET");
			SPECIAL_COLUMNS.put("backend_xmin", "");
		}

	}
	
	public static boolean isCompatiable(String sqlTypename, Object value) {
		if (sqlTypename.equals("VARCHAR")) {
			return String.class.isInstance(value);
		}
		return false;
		
	}
	
	public static boolean isNumberType(String sqlTypeName) {
		return Arrays.asList(NUMERIC_TYPE_NAMES)
				.contains(sqlTypeName.toLowerCase());
	}
	
	public static Class<?> resolveTypeByName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("can not resolve null type name");
		}
		String iname = name.toLowerCase();
		if (typeCatalog.containsKey(iname)) {
			return typeCatalog.get(iname);
		} else {
			throw new RuntimeException(
			"type [" + name + "]" + "can not be resolved. "
			+ " Known types are " + typeCatalog.keySet());
		}
	}
	
	public static boolean isNumber(Object v) {
		if (v == null) return false;
		if (NUMERIC_TYPES.contains(v.getClass()))
			return true;
		if (String.class.isInstance(v)) {
			try {
				Double.parseDouble(String.class.cast(v));
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isText(Object v) {
		if (v == null) return false;
		return String.class.isInstance(v);
	}

	public static boolean isBoolean(Object v) {
		if (v == null) return false;
		try {
			if (Boolean.class.isInstance(v)) return true;
			if (boolean.class.isInstance(v)) return true;
			if ("true".equalsIgnoreCase(v.toString())) return true;
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static String mapDatabaseColumnType(int sqlType, String name) {
		if (SPECIAL_COLUMNS.containsKey(name)) {
			return SPECIAL_COLUMNS.get(name);
		}
		if (!mappings.containsKey(sqlType)) {
			throw new IllegalArgumentException("SQL type code " + sqlType
					+ " is not mapped to any name");
		}
		return mappings.get(sqlType);
	}

}
