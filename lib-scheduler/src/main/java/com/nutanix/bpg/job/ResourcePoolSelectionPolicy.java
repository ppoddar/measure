package com.nutanix.bpg.job;

/**
 * policy to select a {@link ResourcePool pool} 
 * based on category of {@link Job job}.
 * <p>
 * A pool can accept job's of multiple categories.
 * 
 *
 */
public interface ResourcePoolSelectionPolicy {
	/**
	 * gets a pool that accepts job of given category 
	 * @param jobCategory
	 * @return
	 */
	String getPoolByJobCategory(String jobCategory);
}
