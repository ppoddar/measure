package com.nutanix.bpg.measure.swing;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingWorker;

import com.nutanix.bpg.measure.model.Metrics;

/**
 * a specialized {@link SwingWorker} to show metrics
 * as they are collected.
 * 
 * @author pinaki.poddar
 *
 */
public class MetricsViewer extends SwingWorker<Void, Metrics> implements Observer {
	
	public MetricsViewer() {
		super();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		publish();
	}

	@Override
	protected Void doInBackground() throws Exception {
//		plugin.run(Collections.singleton(this));
		return null;
	}
	
	@Override
	public void process(List<Metrics> metrics) {
		System.err.println(metrics);
	}
}
