package com.nutanix.bpg.job;

/**
 * execute job associated with a token.
 * 
 * Result of execution is attached to the token.
 * 
 * @author pinaki.poddar
 *
 */
public interface JobExecutor {
	void execute();
}
