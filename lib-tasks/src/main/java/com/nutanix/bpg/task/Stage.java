package com.nutanix.bpg.task;

import java.util.concurrent.TimeUnit;

public interface Stage extends Iterable<Task>, Runnable {
	boolean isParallel();
	int addTask(Task t);
	void setTimeout(long time, TimeUnit unit);
}
