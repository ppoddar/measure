package com.nutanix.bpg.job.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.ResourcePoolSelectionPolicy;
/**
 * select a pool given  a job description.
 * 
 * @author pinaki.poddar
 *
 */
public class DefaultResourcePoolSelectionPolicy 
	implements ResourcePoolSelectionPolicy {
	private Map<String, String> job2Pool;
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultResourcePoolSelectionPolicy.class);
	
	public DefaultResourcePoolSelectionPolicy() {
		this(new HashMap<>());
	}
	
	public DefaultResourcePoolSelectionPolicy(Map<String, String> map) {
		job2Pool = new HashMap<>(map);
	}

	@Override
	public String getPoolByJobCategory(String jobCategory) {
		return job2Pool.get(jobCategory);
	}
	
	/**
	 * 		Map<String, String> job2Pool = new HashMap<String, String>();
		for (JobTemplate t : jobTemplates) {
			try {
				String poolName = config.getString(t.getName());
				job2Pool.put(t.getName(), poolName);
			} catch (RuntimeException ex) {
				throw ex;
			}
		}

	 */
	

//	@Override
//	public String getQueueByPoolName(String poolName) {
//		return pool2queue.get(poolName);
//	}
//
//	@Override
//	public String getPoolByQueue(String poolName) {
//		return pool2queue.get(poolName);
//	}
//
//	@Override
//	public String getPoolByJobCategory(String jobCategory) {
//		// TODO 
//		throw new AbstractMethodError();
//	}
	

}
