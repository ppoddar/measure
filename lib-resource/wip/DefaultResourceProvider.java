package com.nutanix.resource.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Utilization;
import com.nutanix.capacity.impl.DefaultCapacity;
import com.nutanix.capacity.impl.DefaultUtilization;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourceProvider;

public class DefaultResourceProvider implements ResourceProvider {
	private String id;
	private String name;
	private List<Resource> resources;

	public DefaultResourceProvider() {
		this.resources = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/**
	 * available capacity is sum of available capacity
	 * of all resources.
	 */
	@JsonProperty(required=false)
	@Override
	public Capacity getAvailableCapacity() {
		Capacity cap = newCapacity();
		for (Resource r : this) {
			cap.addCapacity(r.getAvailableCapacity());
		}
		return cap;
	}

	/**
	 * total capacity is sum of total capacity
	 * of all resources.
	 */
	@JsonProperty(required=false)
	@Override
	public Capacity getTotalCapacity() {
		Capacity cap = newCapacity();
		for (Resource r : this) {
			cap.addCapacity(r.getTotalCapacity());
		}
		return cap;
	}

	@Override
	public ResourceProvider addResource(Resource r) {
		if (r == null) {
			throw new IllegalStateException("can not add null resource");
		}
		if (r.getId() == null) {
			throw new IllegalStateException("can not add resource with null id");
		}
		if (r.getId().trim().isEmpty()) {
			throw new IllegalStateException("can not add resource with empty id");
		}
		resources.add(r);
		return this;
	}


	@Override
	public Iterator<Resource> iterator() {
		if (resources == null) {
			return Collections.emptyIterator();
		}
		return resources.iterator();
	}
	
	public List<? extends Resource> getResources() {
		return resources;
	}

	@JsonIgnore
	@Override
	public int getResourceCount() {
		return resources == null ? -1 : resources.size();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Utilization getUtilization() {
		Utilization result = new DefaultUtilization();
		for (Resource r : resources) {
			result = result.accumulate(r.getUtilization());
		}
		
		return result;
	}
	
	public Capacity newCapacity() {
		return new DefaultCapacity();
	}
	
}
