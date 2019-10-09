package com.nutanix.bpg.job.script;
/**
 * Parses a stream line-by-line.
 * Returns an object by parsing or null.
 * 
 * @author pinaki.poddar
 *
 * @param <T> type of object to create by parsing
 */
public interface StreamParser<T> {
	 T parse(String line);
	 T getResult();
}
