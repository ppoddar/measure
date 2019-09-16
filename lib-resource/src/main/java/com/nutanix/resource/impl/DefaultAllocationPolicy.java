package com.nutanix.resource.impl;


import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourcePool;
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
	
	/**
	 * selects the resource wit best fitness.
	 * fitness is sum of fitness for each capacity
	 * of given demand. If any capacity is absent,
	 * or insufficient, overall fitness is -1 
	 * 
	 * @throws RuntimeException is no resoure fits
	 */
	@Override
	public Allocation reserveAllocation(ResourcePool provider, Capacities demand) {
		Allocation alloc = null;
		for (Capacity c : demand) {
			double bestFitness = 0;
			Resource bestFit = null;
			for (Resource rsrc : provider) { 
				double fitness = fitness(rsrc, c);
				if (fitness < 0) break;
				if (fitness > bestFitness) {
					bestFitness = fitness;
					bestFit = rsrc;
				}
			}
			if (bestFit == null) {
				throw new RuntimeException("can not allocate " + c
						+ " Available capcity " + provider.getAvailableCapacity());
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
	public double fitness(Resource supply, Capacity c) {
		double fitness = 0;
			if (!supply.hasKind(c.getKind())) 
				return -1;
			Capacity c2 = supply.getCapacity(c.getKind());
			if (c2.compareTo(c) < 0) return -1;
			fitness += supply.getCapacity(c.getKind())
					.fraction(c);
		return bound(fitness, 1, 0);
	}
	
	double bound(double d, double max, double min) {
		return Math.max(min, Math.min(max, d));
	}
}
