package com.nutanix.bpg.measure.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * General purpose data type conversion.
 * 
 * converts value of one type to another, if possible, with a registered set of
 * {@link Converter data converter}s.
 * 
 * @author pinaki.poddar
 *
 */
public class Conversion {
	static Map<Class<?>, Converter<?>> converters = new HashMap<>();
	private static Map<String, Class<?>> typeCatalog = new HashMap<>();
	/**
	 * an interface contract to convert value of one type to another.
	 * 
	 * @author pinaki.poddar
	 *
	 * @param <T> the type to convert to
	 */
	private interface Converter<T> {
		/**
		 * converts given value to a target type, if possible.
		 * 
		 * @param value an object value. can be null as well.
		 * @return a value of target type
		 * @exception Exception if can not convert value to target type
		 */
		T convert(Object value) throws Exception;
	}

	static class StringConverter implements Converter<String> {
		public String convert(Object value) {
			if (String.class.isInstance(value)) {
				return String.class.cast(value);
			} else {
				return value.toString();
			}
		}
	}

	static class IntegerConverter implements Converter<Integer> {
		public Integer convert(Object value) {
			if (Integer.class.isInstance(value)) {
				return Integer.class.cast(value);
			} else if (Long.class.isInstance(value)) {
				return Long.class.cast(value).intValue();
			} else if (Double.class.isInstance(value)) {
				return Double.class.cast(value).intValue();
			} else if (String.class.isInstance(value)) {
				return new Double(Double.parseDouble(String.class.cast(value))).intValue();
			} else {
				throw new IllegalArgumentException(
						this + " can not convert " + value + " to an instance of Integer class");
			}
		}
	}

	static class DoubleConverter implements Converter<Double> {
		public Double convert(Object value) {
			if (Double.class.isInstance(value)) {
				return Double.class.cast(value);
			} else if (Integer.class.isInstance(value)) {
				return new Double(Integer.class.cast(value).intValue());
			} else if (Long.class.isInstance(value)) {
				return new Double(Long.class.cast(value).longValue());
			} else if (String.class.isInstance(value)) {
				return Double.parseDouble(String.class.cast(value));
			} else {
				throw new IllegalArgumentException(
						this + " can not convert " + value + " to an instance of Double class");
			}
		}
	}

	static class LongConverter implements Converter<Long> {
		public Long convert(Object value) {
			if (Long.class.isInstance(value)) {
				return Long.class.cast(value);
			} else if (Integer.class.isInstance(value)) {
				return new Long(Integer.class.cast(value).intValue());
			} else if (String.class.isInstance(value)) {
				return new Double(Double.parseDouble(String.class.cast(value))).longValue();
			} else {
				throw new IllegalArgumentException(
						this + " can not convert " + value + " to an instance of Long class");
			}
		}
	}

	static class ShortConverter implements Converter<Short> {
		public Short convert(Object value) {
			if (Short.class.isInstance(value)) {
				return Short.class.cast(value);
			} else if (Integer.class.isInstance(value)) {
				return new Short(Integer.class.cast(value).shortValue());
			} else if (String.class.isInstance(value)) {
				return new Double(Double.parseDouble(String.class.cast(value))).shortValue();
		} else {
				throw new IllegalArgumentException(
						this + " can not convert " + value + " to an instance of Short class");
			}
		}
	}

	static class FloatConverter implements Converter<Float> {
		public Float convert(Object value) {
			if (Float.class.isInstance(value)) {
				return Float.class.cast(value);
			} else if (Integer.class.isInstance(value)) {
				return new Float(Integer.class.cast(value).shortValue());
			} else if (String.class.isInstance(value)) {
				return Float.parseFloat(String.class.cast(value));
			} else {
				throw new IllegalArgumentException(
						this + " can not convert " + value + " to an instance of Float class");
			}
		}
	}

	static class CharacterConverter implements Converter<Character> {
		public Character convert(Object value) {
			if (Character.class.isInstance(value)) {
				return Character.class.cast(value);
			} else if (Integer.class.isInstance(value)) {
				return new Character((char) Integer.class.cast(value).intValue());
			} else if (String.class.isInstance(value)) {
				return String.class.cast(value).charAt(0);
			} else {
				throw new IllegalArgumentException(
						this + " can not convert " + value + " to an instance of Character class");
			}
		}
	}

	static class BooleanConverter implements Converter<Boolean> {
		public Boolean convert(Object value) {
			if (Boolean.class.isInstance(value)) {
				return Boolean.class.cast(value);
			} else if (String.class.isInstance(value)) {
				return Boolean.parseBoolean(String.class.cast(value));
			} else {
				throw new IllegalArgumentException(
						this + " can not convert " + value + " to an instance of Boolean class");
			}
		}
	}

    static class UUIDConverter implements Converter<UUID> {
		public UUID convert(Object obj) {
			if (UUID.class.isInstance(obj)) {
				return UUID.class.cast(obj);
			} else if (String.class.isInstance(obj)) {
				return UUID.fromString(String.class.cast(obj));
			} else {
				throw new IllegalArgumentException("can not convert " + obj + " to UUID");
			}
		}
	}

	static class JSONConverter implements Converter<JsonNode> {
		public JsonNode convert(Object obj) throws Exception {
			if (JsonNode.class.isInstance(obj)) {
				return JsonNode.class.cast(obj);
			} else if (String.class.isInstance(obj)) {
				return new ObjectMapper().readTree(String.class.cast(obj));
			} else {
				throw new IllegalArgumentException("can not convert " + obj + " to JSON");
			}
		}
	}
	
	static class TimeunitConverter implements Converter<TimeUnit> {

		@Override
		public TimeUnit convert(Object value) throws Exception {
			if (TimeUnit.class.isInstance(value)) {
				return TimeUnit.class.cast(value);
			} else {
				try {
					return TimeUnit.valueOf(value.toString().toUpperCase());
				} catch (Exception ex) {
					throw new IllegalArgumentException("can not convert " + value + " to timeunit", ex);
				}
			}
		}
		
	}

	static {
		recordConversion("string", String.class, new StringConverter());
		recordConversion("char", Character.class, new CharacterConverter());
		recordConversion("Integer", Integer.class, new IntegerConverter());
		recordConversion("int", Integer.class, new IntegerConverter());
		recordConversion("long", Long.class, new LongConverter());
		recordConversion("short", Short.class, new ShortConverter());
		recordConversion("double", Double.class, new DoubleConverter());
		recordConversion("float", Float.class, new FloatConverter());
		recordConversion("bool", Boolean.class, new BooleanConverter());
		recordConversion("uuid", UUID.class, new UUIDConverter());
		recordConversion("json", JsonNode.class, new JSONConverter());
		recordConversion("time-unit", TimeUnit.class, new TimeunitConverter());
	}

	private static <T> void recordConversion(String name, Class<T> cls, Converter<T> converter) {
		converters.put(cls, converter);
		typeCatalog.put(name.toLowerCase(), cls);
	}

	/**
	 * Converts give value to an instance of target type. 
	 * The converter class is determined
	 * by target type.
	 * 
	 * @param value an arbitrary object
	 * @param cls   a target type
	 * @param <X> requested type
	 * @return a value of requested type
	 */
	@SuppressWarnings("unchecked")
	public static <X> X convert(Object value, Class<X> cls) {
		if (value == null)
			return null;
		if (cls.isInstance(value))
			return cls.cast(value);

		Converter<?> converter = getConverter(cls);
		try {
			return (X) converter.convert(value);
		} catch (Exception ex) {
			throw new RuntimeException("value " + value + " (" + value.getClass()
					+ ") can not be converted to an instance of " + cls + " by " + converter.getClass().getSimpleName(),
					ex);
		}
	}

	public static Set<Class<?>> getSupportedConvertibeTypes() {
		return converters.keySet();
	}


	/**
	 * gets a converter based on given type.
	 * 
	 * @param cls a type to select a converter.
	 * @return a converter for given type
	 */
	public static Converter<?> getConverter(Class<?> cls) {
		if (!converters.containsKey(cls)) {
			for (Class<?> c : converters.keySet()) {
				if (c.isAssignableFrom(cls) || isWrapper(cls,c)) {
					return converters.get(c);
				}
			}
			throw new RuntimeException("no conversion to " + cls + " is supported");
		}
		return converters.get(cls);
	}
	
	public static boolean isWrapper(Class<?> c1, Class<?> c2) {
		if (c1 == int.class && c2 == Integer.class) return true;
		if (c2 == int.class && c1 == Integer.class) return true;
		if (c1 == long.class && c2 == Long.class) return true;
		if (c2 == long.class && c1 == Long.class) return true;
		
		return false;
	}
	

}
