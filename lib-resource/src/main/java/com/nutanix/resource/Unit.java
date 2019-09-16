package com.nutanix.resource;

/**
 * an unit for capacity
 * 
 * @author pinaki.poddar
 *
 */
public interface Unit {
	/**
	 * how many of base unit is equivalent to
	 * one single unit of this receiver
	 * @return
	 */
	double getBaseConversionFactor();
	/**
	 * how many of this unit is equivalent to
	 * one single unit of other.
	 *  
	 * @param other
	 * @return
	 */
	double getConversionFactor(Unit other);
	
	/**
	 * 
	 * @return
	 */
	Resource.Kind getKind();
	Unit getBaseUnit();
	String getSymbol();
	
}
