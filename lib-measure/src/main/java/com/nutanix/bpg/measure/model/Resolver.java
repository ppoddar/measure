package com.nutanix.bpg.measure.model;

/**
 * resolves by name.
 * 
 * @author pinaki.poddar
 *
 */
public interface Resolver {
	<T> T resolve(String name, Class<T> t);
}
