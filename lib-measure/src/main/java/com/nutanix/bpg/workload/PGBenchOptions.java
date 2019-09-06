package com.nutanix.bpg.workload;

import java.util.concurrent.TimeUnit;

public class PGBenchOptions {
	private int scaleFactor;
	private int timeToRun;
	private TimeUnit timeToRunUnit;
	private boolean init;
	public static String SPACE  = " ";
	public static final String OPTION_DATABASE = "-d";
	public static final String OPTION_SCALE    = "-s";
	public static final String OPTION_INIT     = "-i";
	public static final String OPTION_TIME_TO_RUN  = "-T";
	
	public PGBenchOptions() {
		scaleFactor = 1;
		timeToRun = 1;
		timeToRunUnit = TimeUnit.MINUTES;
		init = true;
	}
	
	public int getScaleFactor() {
		return scaleFactor;
	}
	public int getTimeToRun() {
		return timeToRun;
	}
	public TimeUnit getTimeToRunUnit() {
		return timeToRunUnit;
	}
	public boolean isInit() {
		return init;
	}
	public void setScaleFactor(int scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	public void setTimeToRun(int timeToRun) {
		this.timeToRun = timeToRun;
	}
	public void setTimeToRunUnit(TimeUnit timeToRunUnit) {
		this.timeToRunUnit = timeToRunUnit;
	}
	public void setInit(boolean init) {
		this.init = init;
	}
	
	
	public String[] getInitCommandOptions(String db) {
		return new String[] {OPTION_INIT,  OPTION_DATABASE, db};
	}
	
	public String[] getCommandOptions(String db) {
		return new String[] {
			OPTION_TIME_TO_RUN, ""+timeToRun,
			OPTION_DATABASE, db};
	}
	
}
