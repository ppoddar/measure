package com.nutanix.resource.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Quantity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;
import com.nutanix.resource.Utilization;

/**
 * abstract resource maintains capacity management 
 * aspect of a resource.
 * 
 * A resource maintains its capacity for multiplle
 * {@link Resource.Kind kinds} e.g.
 * {@link Resource.Kind#MEMORY MEMORY}, 
 * {@link Resource.Kind#STORAGE STORAGE},
 * or {@link Resource.Kind#COMPUTE COMPUTE} etc.
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class AbstarctResource implements Resource {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		AbstarctResource other = (AbstarctResource) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	private String uuid;
	private String name;
	private Capacity limit     = new DefaultCapacity();
	private Capacity available = new DefaultCapacity();
	private Map<Resource.Kind, Unit> preferredUnits =
			new HashMap<Resource.Kind, Unit>();
	
	protected AbstarctResource(String id) {
		setId(id);
	}
	
	@Override
	public final String getId() {
		return uuid;
	}
	
	protected void setId(String id) {
		this.uuid = id;
	}

	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	
	@Override
	public Unit getUnit(Kind kind) {
		return preferredUnits.get(kind);
	}

	
	@JsonProperty(required=false)
	@Override
	public Capacity getAvailableCapacity() {
		return available;
	}
	

	@Override
	public boolean hasKind(Resource.Kind kind) {
		return available.hasKind(kind);
	}

	@Override
	public Collection<Resource.Kind> getKinds() {
		return available.getKinds();
	}
	
	@Override
	public final Resource addQuanity(Quantity q) {
		q = toPreferredUnit(q);
		available.addQuantity(q);
		limit.addQuantity(q);
		return this;
	}
	

	@JsonProperty(required=false)
	@Override
	public Capacity getTotalCapacity() {
		return limit;
	}

	@Override
	public Resource reduceCapacity(Capacity cap) {
		for (Quantity q : cap) {
			available.reduceQuantity(toPreferredUnit(q));
		}
		return this;
	}

    protected void setPreferredUnit(Resource.Kind kind, Unit unit) {
    	preferredUnits.put(kind, unit);
    }
    
    protected Quantity toPreferredUnit(Quantity q) {
    	Unit preferredUnit = this.getUnit(q.getKind());
    	if (q.getUnit() == preferredUnit) {
    		return q;
    	} else {
    		return q.convert(preferredUnit);
    	}
    }
    
    public Utilization getUtilization() {
    	Utilization result = new DefaultUtilization();
    	for (Resource.Kind kind : getKinds()) {
    		double available = getAvailableCapacity().getQuantity(kind).getValue();
    		double total     = getTotalCapacity().getQuantity(kind).getValue();
    		double used = total - available;
    		double u = used/total;
    		result.put(kind, u);
    	}
    	return result;
    }

}
