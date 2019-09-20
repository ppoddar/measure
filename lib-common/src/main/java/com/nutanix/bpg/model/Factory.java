package com.nutanix.bpg.model;

import java.io.InputStream;

/**
 * A factory builds instance of generic type
 * from a stream.
 * 
 * @author pinaki.poddar
 *
 * @param <T> type of things built
 */
public interface Factory<T> {
	T build(InputStream in) throws Exception;
	Class<T> getType();
}
