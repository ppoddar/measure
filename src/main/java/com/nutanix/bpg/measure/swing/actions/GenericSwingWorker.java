package com.nutanix.bpg.measure.swing.actions;

import javax.swing.SwingWorker;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.swing.MeasurementGUI;
import com.nutanix.bpg.measure.swing.dialogs.Wizard;

public abstract class GenericSwingWorker<T, V> extends SwingWorker<T, V> {
	protected final Wizard wizard;
	protected final MeasurementServer server;
	
	protected GenericSwingWorker(Wizard w) {
		this.wizard = w;
		this.server = MeasurementGUI.instance().getServer();
		
	}

}
