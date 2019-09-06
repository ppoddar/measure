package com.nutanix.bpg.measure;

import java.util.concurrent.TimeUnit;

public interface Schedule extends Runnable {
	int getCount();
	int getInterval();
	TimeUnit getIntervalTimeUnit();
}
