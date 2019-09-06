package com.nutanix.bpg.measure.script;

@SuppressWarnings("serial")
public class ProcessStartException extends RuntimeException {
	public ProcessStartException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
