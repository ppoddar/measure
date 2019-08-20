package com.nutanix.bpg.measure.swing.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.swing.MeasurementGUI;
import com.nutanix.bpg.measure.swing.actions.TakeSnapshot;

@SuppressWarnings("serial")
public class TakeSnapshotWizard extends Wizard {
	public static Logger logger = LoggerFactory.getLogger(TakeSnapshotWizard.class);

	public TakeSnapshotWizard() {
		super(MeasurementGUI.instance(), 
			new TakeSnapshotWizardPage());
		setName(getClass().getSimpleName());
	}
	@Override
	public void performAction() {
		new TakeSnapshot(this).execute();
	}

}
