package com.nutanix.bpg.job;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.utils.StringUtils;
/**
 * select a pool given  a job description.
 * 
 * @author pinaki.poddar
 *
 */
public class DefaultResourcePoolSelectionPolicy implements ResourcePoolSelectionPolicy {
	private Map<String, String> jobCategry2Pool;
	private static final Logger logger = LoggerFactory.getLogger(DefaultResourcePoolSelectionPolicy.class);
	
	public DefaultResourcePoolSelectionPolicy(Map<String, String> map) {
		this.jobCategry2Pool = new HashMap<String, String>(map);
	}
	
	@Override
	public String select(String jobCategory) {
		logger.debug("selecting pool for job category " + jobCategory);
		if (StringUtils.isEmpty(jobCategory)) {
			throw new IllegalArgumentException("can not select pool for job with null/empty category");
		}
		if (!jobCategry2Pool.containsKey(jobCategory)) {
			throw new RuntimeException("can not select a pool "
			+ " by job category [" + jobCategory + "]"
			+ "available pools are " + jobCategry2Pool.values());
		}
		String poolName = jobCategry2Pool.get(jobCategory);
		return poolName;
	}
	

}
