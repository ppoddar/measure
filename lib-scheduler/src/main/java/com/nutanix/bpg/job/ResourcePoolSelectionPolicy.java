package com.nutanix.bpg.job;

/**
 * policy to select a {@link ResourcePool pool} 
 * based on {@link Job job}.
 * 
 * @author pinaki.poddar
 *
 */
public interface ResourcePoolSelectionPolicy {
	/**
	 * select a pool given a job 
	 * @param job must not be null
	 * @return a pool. never null
	 */
	String select(String jobCategory);
}
