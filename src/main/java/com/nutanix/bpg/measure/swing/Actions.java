package com.nutanix.bpg.measure.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import com.nutanix.bpg.measure.swing.dialogs.TakeSnapshotWizard;
import com.nutanix.bpg.measure.swing.dialogs.ShowBaselineWizard;
import com.nutanix.bpg.measure.swing.dialogs.ShowSnapshotWizard;
import com.nutanix.bpg.measure.swing.dialogs.Wizard;


/**
 * Declares all actions supported by Measurement Service.
 * 
 * @author pinaki.poddar
 *
 */
public class Actions {
	public static Action DATABASE_NEW      = new NewDatabase();
	public static Action SNAPSHOT_CURRENT  = new CurrentSnapshot();
	public static Action SNAPSHOT_NEW      = new TakeSnapshot();
	public static Action SNAPSHOT_SHOW     = new ShowSnapshot();
	public static Action SNAPSHOT_BASELINES = new FetchBaselines();
	public static Action EXIT              = new ExitAction();
	
	private static void runWizard(Wizard wizard) {
		int response = wizard.openDialog();
		if (response == JOptionPane.OK_OPTION) {
			wizard.performAction();
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewDatabase extends AbstractAction {
		public NewDatabase() {
			super("New");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "hello");
		}
		
	}
	
	@SuppressWarnings("serial")
	private static class ExitAction extends AbstractAction {
		public ExitAction() {
			super("Exit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	
	@SuppressWarnings("serial")
	private static class TakeSnapshot extends AbstractAction {
		public TakeSnapshot() {
			super("Take a measurment...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Actions.runWizard(new TakeSnapshotWizard());
		}
	}
	
	@SuppressWarnings("serial")
	private static class ShowSnapshot extends AbstractAction {
		public ShowSnapshot() {
			super("Show Snapshots...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Actions.runWizard(new ShowSnapshotWizard());
		}
	}
	
	@SuppressWarnings("serial")
	private static class CurrentSnapshot extends AbstractAction {
		public CurrentSnapshot() {
			super("Show ...");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Actions.runWizard(new ShowSnapshotWizard());
		}
	}
	
	@SuppressWarnings("serial")
	private static class FetchBaselines extends AbstractAction {
		public FetchBaselines() {
			super("Baselines");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Actions.runWizard(new ShowBaselineWizard());

		}
	}
}
