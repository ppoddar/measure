package com.nutanix.bpg.measure.swing.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.swing.MeasurementGUI;
import com.nutanix.bpg.measure.swing.actions.ShowSnapshot;

@SuppressWarnings("serial")
public class ShowSnapshotWizard extends Wizard {
	public static Logger logger = LoggerFactory.getLogger(ShowSnapshotWizard.class);
	public ShowSnapshotWizard() {
		super(MeasurementGUI.instance(), new SnapshotQueryPage());
	}
	
	@Override
	public void performAction() {
		new ShowSnapshot(this).execute();
	}

}
