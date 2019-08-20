package com.nutanix.bpg.measure.swing.dialogs;

import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.jdbc.SnapshotPlugin;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.swing.Images;
import com.nutanix.bpg.measure.swing.MeasurementGUI;
import com.nutanix.bpg.measure.swing.widgets.NamedComboBox;
import com.nutanix.bpg.measure.swing.widgets.SnapshotScheduleView;

import net.miginfocom.swing.MigLayout;


@SuppressWarnings("serial")
public class TakeSnapshotWizardPage extends WizardPage {
	private JTextField name;
	
	private JComboBox<Metrics> metrics;
	private JComboBox<Database> databases;
	private SnapshotScheduleView schedule;
			
	private static Logger logger = LoggerFactory.getLogger(TakeSnapshotWizard.class);
	private static final String SNAPSHOT_DESCRIPTION     = "snapshot.description";
	
	public TakeSnapshotWizardPage() {
		super();
		ResourceBundle resources = MeasurementGUI.instance().getResources();
		MeasurementServer server = MeasurementGUI.instance().getServer();
		setLayout(new MigLayout("insets 20")); 

		add(new JLabel(resources.getString(SnapshotPlugin.PARAM_SNAPSHOT_NAME)));
		add(name = new JTextField(24), "wrap");
		
		add(new JLabel(resources.getString(SnapshotPlugin.PARAM_METRICS)));
		
		metrics = new NamedComboBox<Metrics>(server.getMetrices());
		add(metrics, "wrap");
		
		add(new JLabel(resources.getString(SnapshotPlugin.PARAM_DATABASE)));
		add(databases = new NamedComboBox<Database>(server.getDatabases()), "wrap");
		
		add(new JLabel("Snapshot schedule"));
		add(schedule = new SnapshotScheduleView());
	}
	
	@Override
	public ImageIcon getIcon() {
		return Images.ICON_SNAPSHOT;
	}
	
	@Override
	public String getDescription() {
		return MeasurementGUI.instance()
			.getResources()
			.getString(SNAPSHOT_DESCRIPTION);
	}
	
	




	@Override
	public Object getUserInput(String key) {
		switch (key) {
		case SnapshotPlugin.PARAM_SNAPSHOT_NAME:
			return name.getText();
		case SnapshotPlugin.PARAM_DATABASE:
			return databases.getSelectedItem();
		case SnapshotPlugin.PARAM_METRICS:
			return metrics.getSelectedItem();
		case SnapshotPlugin.PARAM_SCHEDULE:
			return schedule.getValue();
		default:
			return null;
		}
	}

	@Override
	public boolean verifyUserInputs() {
		logger.debug("verifying " + this);
		return true;
	}


}
