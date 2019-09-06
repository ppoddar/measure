package com.nutanix.bpg.measure.script;

import java.util.Observable;

public class MetricsPrinter extends CallbackAdapter<Void> {

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("" + arg);
	}

}
