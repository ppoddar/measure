package com.nutanix.bpg.job.impl;

import com.nutanix.bpg.job.Task;

public class DefaultTask implements Task {

	@Override
	public Boolean call() throws Exception {
		System.err.println(this + "call");
		return true;
	}

	@Override
	public long getExpectedCompletionTimeInMillis() {
		return 1000;
	}

}
