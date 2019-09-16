package com.nutanix.resource.model;

import com.nutanix.bpg.measure.model.Named;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultResourceProvider;

/**
 * represents a Nutanix cluster.
 * A cluster supplies {@link com.nutanix.resource.Resource
 * resource} to a {@link com.nutanix.resource.ResourcePool pool}.
 * A cluster uses {@link PrismConnect connection} to
 * Nutanix cluster via Prism gateway, issues HTTPS requests,
 * parses JSON response  and populates resource pool.
 *  
 * @author pinaki.poddar
 *
 */
public class Cluster extends DefaultResourceProvider 
	implements Named, ResourceProvider {
	private String name;
	private String host;
	private int    port;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHost() {
		return host;
	}
	
	public Cluster setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	
	public String toString() {
		return getName() + "@" + getHost() + ":" + getPort();
	}

	
}
