package com.nutanix.resource.model;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.bpg.utils.Named;
import com.nutanix.resource.Resource;
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
	private String user;
	private String password;
	private String hypervisor;
	private boolean available;
	private String reason;
	
	@JsonCreator
	public Cluster(@JsonProperty("id") String id) {
		super(id);
		available = true;
	}
	
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString() {
		return getName() + "@" + getHost() + ":" + getPort();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}

	public void markUnavailable(String cause) {
		available = false;
		reason = cause;
	}
	
	public boolean isAvalable() {
		return available;
	}
	
	public String getReasonForUnavailability() {
		return reason;
	}
}
