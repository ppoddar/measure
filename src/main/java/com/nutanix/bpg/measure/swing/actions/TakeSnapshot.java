package com.nutanix.bpg.measure.swing.actions;

import javax.swing.JOptionPane;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.jdbc.SnapshotPlugin;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.measure.swing.Images;
import com.nutanix.bpg.measure.swing.dialogs.ErrorDialog;
import com.nutanix.bpg.measure.swing.dialogs.Wizard;

public class TakeSnapshot extends GenericSwingWorker<Snapshot, String> {
	public TakeSnapshot(Wizard w) {
		super(w);
	}

	@Override
	protected Snapshot doInBackground() throws Exception {
		try {
		  return server.takeSnapshot(
				  wizard.getUserInput(SnapshotPlugin.PARAM_SNAPSHOT_NAME, String.class),
				  wizard.getUserInput(SnapshotPlugin.PARAM_METRICS, Metrics.class),
				  wizard.getUserInput(SnapshotPlugin.PARAM_DATABASE, Database.class),
				  wizard.getUserInput(SnapshotPlugin.PARAM_SCHEDULE, SnapshotSchedule.class));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void done() {
		try {
			Snapshot snapshot = get();
			String msg = "Snapshot (uuid=..." + snapshot.getId().substring(snapshot.getId().length()-3) + ")" 
					+ " has started";
			JOptionPane.showMessageDialog(
					null, msg,
					"Snapshot created",
					JOptionPane.INFORMATION_MESSAGE,
					Images.ICON_SNAPSHOT);
					
		} catch (Exception e) {
			new ErrorDialog().showError(e);
		}
	}
}


