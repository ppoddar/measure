package com.nutanix.resource.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import com.nutanix.resource.Allocation;
import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;

public class DefaultAllocation implements Allocation {
	private String id;
	private long startTime;
	private Duration duration;
	private final Capacities demand;
	private final Resource   supply;
	
	public DefaultAllocation(Capacities demand, Resource   supply) {
		if (demand == null) 
			throw new IllegalArgumentException("can not allocate for null demand");
		if (supply == null) 
			throw new IllegalArgumentException("can not allocate for null supply");
		this.demand = demand;
		this.supply = supply;
	}
	
	public String getId() {
		return id;
	}
	
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public Duration getDuration() {
		return duration;
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	@Override
	public long getEndTime() {
		return startTime + duration.getSeconds()*1000;
	}

	@Override
	public int compareTo(Allocation o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Resource getSupply() {
		return supply;
	}

	@Override
	public Capacities getDemand() {
		return demand;
	}
	
	
}
