package com.nutanix.bpg.measure.model;

import java.io.InputStream;

/**
 * A factory builds instance of generic type
 * from a stream.
 * 
 * @author pinaki.poddar
 *
 * @param <T>
 */
public interface Factory<T> {
	T build(InputStream in) throws Exception;
	Class<T> getType();
}
