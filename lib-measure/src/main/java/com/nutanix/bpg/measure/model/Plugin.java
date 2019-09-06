package com.nutanix.bpg.measure.model;

import java.util.concurrent.Callable;
/**
 * A plug-in performs an (unspecified) action and notifies
 * one or more {@link Observer observers} via 
 * {@link Observer#update(java.util.Observable, Object)
 * update()} method.
 * <p>
 * The observer receives update as an object. The observer
 * is responsible to interpret/consume/parse the object
 * and perform any subsequent operation on the received
 * object.
 * 
 * @author pinaki.poddar
 *
 */
public interface Plugin<T> extends Callable<T> {
	/**
	 * runs this plug-in.
	 * The effect of running this plug-in is unspecified.
	 * <p>
	 * The typical usage, at time of this writing, is to take
	 * a {@link Measurement}. 
	 * 
	 *  on given database to collect some
	 * metrics. A plug-in generates metrics as simple name-value
	 * pairs. These name-value pairs are notified to 
	 * callbacks (observers).
	 * <p>
	 * The observers parse name-value
	 * pairs because they have knowledge about the name-value pairs
	 * @param args run time variables
	 * @param ctx TODO
	 * 
	 * @throws Exception if unexpected exception happes.
	 * Typically, exceptions are notified to one of the callbacks.
	 * 
	 */
	//public void run(Map<String, Object> args, Object ctx) throws Exception;
	
	/**
	 * gets metadata about this plug-in.
	 * Metadata captures all static configuration about a plug-in.
	 * @return meta data about this receiver. never null.
	 */
	//PluginMetadata getDescriptor();
	
	
	/**
	 * configures this plug-in. {@link PluginMetadata Metadata} about
	 * a plug-in carries all design-time configuration.
	 * The runtime configuration is used in this moment.
	 * @param props key-value pairs
	 * @param <P> type of plug-in. 
	 * @return a plug-in configured with given map
	 */
	//<P extends Plugin> P customize(Map<String, String> props);
}
