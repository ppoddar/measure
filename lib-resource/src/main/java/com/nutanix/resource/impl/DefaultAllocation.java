package com.nutanix.resource.impl;

import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.Resource;
import com.nutanix.capacity.Capacity;

public class DefaultAllocation implements Allocation {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DefaultAllocation other = (DefaultAllocation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	

	private String id;
	private long startTime = -1;
	private Duration duration;
	private Capacity demand;
	private Resource supply;
	
	public DefaultAllocation() {
		this(UUID.randomUUID().toString());
	}

	@JsonCreator
	public DefaultAllocation(@JsonProperty("id") String id) {
		this.id = id;
	}
	public void setDemand(Capacity demand) {
		this.demand = demand;
	}
	public void setSupply(Resource supply) {
		this.supply = supply;
	}

	private static final Logger logger = LoggerFactory.getLogger(DefaultAllocation.class);
	
	
	public DefaultAllocation(Capacity demand, Resource   supply) {
		if (demand == null) 
			throw new IllegalArgumentException("can not allocate for null demand");
		if (supply == null) 
			throw new IllegalArgumentException("can not allocate for null supply");
		this.demand = demand;
		this.supply = supply;
		logger.debug("creating allocation " + this);
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
		if (duration == null) return -1;
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
	public Capacity getDemand() {
		return demand;
	}
	
	public String toString() {
		return "demand " + getDemand() + " allocated to "  + getSupply();
	}
	
}
