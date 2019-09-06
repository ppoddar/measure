package com.nutanix.bpg.workload;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.script.StreamParser;

/**
 * a stateful parser receives lines from pgbench.
 * the lines are buffered.
 * 
 * @author pinaki.poddar
 *
 */


public /* for testing */ 
class PGBenchoutputParser implements StreamParser<Measurement> {
	static List<Signature> signatures = new ArrayList<Signature>();
	static {
		signatures.add(new Signature("scale", "(.*?)scaling factor: (?<value>\\d+)(.*?)"));
		signatures.add(new Signature("tps",   "(.*?)tps = (?<value>.+)\\(including(.*)"));
		signatures.add(new Signature("tps",   "(.*?)tps = (?<value>.+)\\(excluding(.*)", true));
	}
	private int lineCount;
	private Measurement result;
	private Measurement partial;
	
	private static Logger logger = LoggerFactory.getLogger(PGBenchoutputParser.class);
	
	public PGBenchoutputParser() {
		partial = newMeasurement();
	}
	
	Measurement newMeasurement() {
		Measurement m = new Measurement(PGBench.METRICS);
		return m;
	}
	
	public Measurement getResult() {
		if (result == null) {
			String msg = "output parser have not parsed a measuremnt";
			throw new IllegalStateException(msg 
				+ " " + lineCount + " lines of output process stream " 
						+ " has been parsed");
			
		}
		return result;
	}

	
	/**
	 * parses given line.  
	 * A measurement collected over multiple line.
	 * 
	 * @param line
	 * @return a {@link Measurement} if parsing is complete.
	 * Else null.
	 */
	public Measurement parse(String line) {
		lineCount++;
		logger.trace("parse " + line);
		Signature sign = match(line);
		if (sign == null) return null;
		
		Object value = sign.parse(line);
		logger.trace(sign + "matches with value=" + value);
		partial.putValue(sign.getKey(), value);
		if (sign.isTerminating()) {
			try {
				partial.setEndTime(System.currentTimeMillis());
				result = partial.clone();
				logger.trace("terminating " + sign + " matches. Returning " + result);
				return result; 
			} finally {
				partial = newMeasurement();
			}
		}
		return null;
	}
	
	/**
	 * tries each signature.
	 * @param line a line
	 * @return signature that matches. null otherwise.
	 */
	Signature match(String line) {
		for (Signature sign : signatures) {
			if (sign.matches(line)) {
				logger.trace("signature " + sign + " matched " + line);
				return sign;
			}
		}
		logger.trace("no signature matched " + line);
		return null;
	}
	
	/**
	 * a signature to match a line
	 * @author pinaki.poddar
	 *
	 */
	public static class Signature {
		final String key;
		final Pattern sign;
		final boolean isTeminating;
		
		public Signature(String key, String sign) {
			this(key, sign, false);
		}
		
		public Signature(String key, String sign, boolean flag) {
			this.key = key;
			this.sign = Pattern.compile(sign, Pattern.DOTALL);
			this.isTeminating = flag;
					
		}
		
		/**
		 * 
		 * @param line
		 * @return
		 */
		public Object parse(String line) {
			Matcher matcher = sign.matcher(line);
			if (!matcher.matches()) {
				throw new RuntimeException(this + " does not match " + line);
			}
			String value = matcher.group("value");
			return value;
		}
		
		public String getKey() {
			return key;
		}
		
		
		public boolean matches(String line) {
			Matcher matcher = sign.matcher(line);
			return matcher.matches();
		}
		
		public boolean isTerminating() {
			return isTeminating;
		}
		
		public String toString() {
			return "signature:" + getKey();
		}
	}
}
