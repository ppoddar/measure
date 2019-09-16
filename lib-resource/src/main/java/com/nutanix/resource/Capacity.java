package com.nutanix.resource;
/**
 * amount of something in a specific unit
 * 
 * @author pinaki.poddar
 *
 */
public interface Capacity extends Comparable<Capacity>{
	Capacity clone(double amount);
	
	Resource.Kind getKind();
	double getAmount();
	Unit   getUnit();
	Capacity convert(Unit to);
	boolean isIntegral();
	
	Capacity plus(Capacity other);
	Capacity minus(Capacity other);
	Capacity times(int n);
	double fraction(Capacity other);
}
