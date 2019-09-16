package com.nutanix.resource.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nutanix.resource.Capacities;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourceProvider;

public class DefaultResourceProvider implements ResourceProvider {
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	private List<Resource> resources = new ArrayList<Resource>();

	/**
	 * available capacity is sum of available capacity
	 * of all resources.
	 */
	@Override
	public Capacities getAvailableCapacities() {
		Capacities cap = new DefaultCapacities();
		for (Resource r : this) {
			cap.addCapacities(r.getCapacities());
		}
		return cap;
	}

	/**
	 * total capacity is sum of total capacity
	 * of all resources.
	 */
	@Override
	public Capacities getTotalCapacities() {
		Capacities cap = new DefaultCapacities();
		for (Resource r : this) {
			cap.addCapacities(r.getMaxCapacities());
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
		return resources.iterator();
	}
}
