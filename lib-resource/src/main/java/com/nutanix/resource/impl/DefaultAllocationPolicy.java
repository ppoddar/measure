package com.nutanix.resource.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourcePool;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Quantity;
/**
 * default implementation of {@link AllocationPolicy 
 * allocation policy} selects a resource from the pool,
 * that has best {@link #fitness(Resource, Capacity) fitness}
 * for given demand.
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class DefaultAllocationPolicy implements AllocationPolicy {
	private static Logger logger = LoggerFactory.getLogger(DefaultAllocationPolicy.class);
	/**
	 * selects the resource wit best fitness.
	 * fitness is sum of fitness for each capacity
	 * of given demand. If any capacity is absent,
	 * or insufficient, overall fitness is -1 
	 * 
	 * @throws RuntimeException is no resource fits
	 */
	@Override
	public Allocation reserveAllocation(ResourcePool provider, Capacity demand) {
		Allocation alloc = null;
		logger.debug("allocating " + demand + " with provider " + provider.getName() + " with capacity " + provider.getTotalCapacity());
		for (Quantity c : demand) {
			double bestFitness = 0;
			Resource bestFit = null;
			for (Resource rsrc : provider) { 
				double fitness = fitness(rsrc, c);
				//logger.debug("fitness=" + fitness + " resource " + rsrc.getId() + " available capacity " + rsrc.getAvailableCapacity());
				if (fitness < 0) continue;
				if (fitness > bestFitness) {
					//logger.debug("found candidate resource " + rsrc + " with fitness " + fitness + " for quantity " + c);
					bestFitness = fitness;
					bestFit = rsrc;
				}
			}
			if (bestFit == null) {
				throw new RuntimeException("can not allocate " + c
						+ " Available capcity " 
						+ provider.getAvailableCapacity());
			}
			alloc = new DefaultAllocation(demand, bestFit);
		}
		return alloc;
	}
	
	/**
	 * gets a fitness factor on given capacity fits
	 * to given resource.
	 * If resource does not either have kind of capacity,
	 * or available capacity is insufficient, 
	 * then returns negative number.
	 *  
	 * @param supply a candidate resource 
	 * @param c a demand
	 * @return a number between 0 and 1. 
	 */
	public double fitness(Resource supply, Quantity c) {
		double fitness = 0;
		Quantity c2 = supply.getAvailableCapacity().getQuantity(c.getKind());
		if (c2.compareTo(c) < 0) return -1;
		fitness += supply.getAvailableCapacity().getQuantity(c.getKind())
					.fraction(c);
		return bound(fitness, 1, 0);
	}
	
	double bound(double d, double max, double min) {
		return Math.max(min, Math.min(max, d));
	}
}
