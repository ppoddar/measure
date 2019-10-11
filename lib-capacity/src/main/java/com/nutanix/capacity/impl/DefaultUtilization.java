package com.nutanix.capacity.impl;

import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Unit;
import com.nutanix.capacity.Utilization;

/**
 * 
 * @author pinaki.poddar
 *
 */
public class DefaultUtilization implements Utilization {
	ResourceKind kind;
	Quantity total;
	Quantity used;
	Quantity free;
	
	public DefaultUtilization(ResourceKind kind,
			double total, double used, double free,
			Unit unit) {
		this.total = kind.newQuantity(total, unit);
		this.used = kind.newQuantity(used, unit);
		this.free = kind.newQuantity(free, unit);
	}
	public DefaultUtilization(Quantity total, Quantity used, Quantity free) {
		this.kind = total.getKind();
		this.total = total;
		this.used =  used.convert(total.getUnit());
		this.free =  free.convert(total.getUnit());
	}
	
	
	@Override
	public ResourceKind getKind() {
		return kind;
	}

	@Override
	public Quantity getTotal() {
		return total;
	}

	@Override
	public Quantity getUsed() {
		return used;
	}

	@Override
	public Quantity getAvailable() {
		return free;
	}
	
	@Override
	public double get() {
		return total.fraction(used);
	}
	@Override
	public Utilization accumulate(Utilization other) {
		if (other == null) return this;
		return new DefaultUtilization(
				total.plus(other.getTotal()), 
				used.plus(other.getUsed()), 
				free.plus(other.getAvailable()));
		
	}
}
