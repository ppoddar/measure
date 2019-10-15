package com.nutanix.resource.impl;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.capacity.Capacity;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.Resource;

public class DefaultAllocation implements Allocation {
	private String id;
	private long startTime = -1;
	private Duration duration;
	private final Capacity demand;
	private final Resource supplier;
	private static final Logger logger = LoggerFactory.getLogger(DefaultAllocation.class);
	
	public DefaultAllocation(Capacity demand, Resource supplier) {
		if (demand == null) 
			throw new IllegalArgumentException("can not allocate for null demand");
		if (supplier == null) 
			throw new IllegalArgumentException("can not allocate to null supplier");
		this.demand = demand;
		this.supplier = supplier;
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
	public Resource getSupplier() {
		return supplier;
	}

	@Override
	public Capacity getDemand() {
		return demand;
	}
	
	public String toString() {
		return "demand " + getDemand() + " allocated to "  + getSupplier();
	}
	
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
	


	
}
