package com.nutanix.bpg.measure.swing.dialogs;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.swing.MeasurementGUI;

@SuppressWarnings("serial")
public class ShowBaselineWizard extends Wizard {
	public static Logger logger = LoggerFactory.getLogger(ShowBaselineWizard.class);

	/**
	 * creates a wizard to take snapshot measurment. 
	 */
	public ShowBaselineWizard() {
		super(MeasurementGUI.instance(), 
			new TakeSnapshotWizardPage());
		setName(getClass().getSimpleName());
	}

	/**
	 * perform action. 
	 * the pages have collected user inputs
	 */
	@Override
	public void performAction() {
		MeasurementServer server = MeasurementGUI.instance().getServer();
		logger.info("performAction()");
		SwingWorker<List<Snapshot>, String> worker = new SwingWorker<List<Snapshot>, String>() {
			@Override
			protected List<Snapshot> doInBackground() throws Exception {
				
				return null;
			}
			
			@Override
			public void done() {
				try {
					List<Snapshot> snapshot = get();
					JOptionPane.showMessageDialog(ShowBaselineWizard.this, 
							"snapshot " + snapshot + " has started");
				} catch (Exception e) {
					new ErrorDialog().showError(e);
				}
			}
		};
		worker.execute();
	}

}
