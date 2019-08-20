package com.nutanix.bpg.measure.swing.actions;

import java.util.Date;
import java.util.List;

import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.swing.MeasurementGUI;
import com.nutanix.bpg.measure.swing.dialogs.ErrorDialog;
import com.nutanix.bpg.measure.swing.dialogs.SnapshotQueryPage;
import com.nutanix.bpg.measure.swing.dialogs.Wizard;
import com.nutanix.bpg.measure.swing.widgets.SnapshotView;

/**
 * Fetches snapshots and dispays them in a {@link SnapshotView}.
 * 
 * @author pinaki.poddar
 *
 */
public class ShowSnapshot extends GenericSwingWorker<List<Snapshot>, String> {
	public static Logger logger = LoggerFactory.getLogger(ShowSnapshot.class);

	public ShowSnapshot(Wizard w) {
		super(w);
	}
	
	@Override
	protected List<Snapshot> doInBackground() throws Exception {
		Date startDate = null;
		try {
			startDate = wizard.getUserInput(SnapshotQueryPage.PARAM_START_DATE, Date.class);
		} catch (Exception ex) {}
		Date endDate = null;
		try {
			endDate   = wizard.getUserInput(SnapshotQueryPage.PARAM_END_DATE, Date.class);
		} catch (Exception ex) {}
		try {
		  return server.getSnapshotsByTimeRange(
				  startDate == null ? null : startDate.getTime(),
				  endDate == null ? null : endDate.getTime());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void done() {
		try {
			List<Snapshot> snapshots = get();
			final Metrics metrics = wizard.getUserInput(SnapshotQueryPage.PARAM_METRICS, Metrics.class);
			logger.info("got " + snapshots.size() + " items\n" + snapshots);
			SnapshotView view = new SnapshotView(metrics, snapshots);
			JTabbedPane tabs = MeasurementGUI.instance().getTab();
			tabs.addTab("snapshot", view);
		} catch (Exception e) {
			new ErrorDialog().showError(e);
		}
	}
}

