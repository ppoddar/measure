package com.nutanix.capacity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * an unit for capacity
 * 
 * @author pinaki.poddar
 *
 */
public interface Unit extends Comparable<Unit> {
	/**
	 * how many of base unit is equivalent to
	 * one single unit of this receiver
	 * @return
	 */
	@JsonIgnore
	double getBaseConversionFactor();
	/**
	 * how many of this unit is equivalent to
	 * one single unit of other.
	 *  
	 * @param other
	 * @return
	 */
	@JsonIgnore
	double getConversionFactor(Unit other);
	
	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	ResourceKind getKind();
	
	@JsonValue
	String getSymbol();
		
}
