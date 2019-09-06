package com.nutanix.bpg.workload;

import java.util.Map;
import java.util.concurrent.Callable;

public interface Workload extends Callable<Exception> {
	public void configure(Map<String, Object> vars);
}
