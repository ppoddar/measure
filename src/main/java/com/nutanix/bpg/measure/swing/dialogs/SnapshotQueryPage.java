package com.nutanix.bpg.measure.swing.dialogs;

import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.jdbc.SnapshotPlugin;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.swing.Images;
import com.nutanix.bpg.measure.swing.MeasurementGUI;
import com.nutanix.bpg.measure.swing.widgets.NamedComboBox;
import com.nutanix.bpg.measure.swing.widgets.TimeInput;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class SnapshotQueryPage extends WizardPage {
	public static final String PARAM_METRICS    = "metrics";
	public static final String PARAM_START_DATE = "start-date";
	public static final String PARAM_END_DATE   = "end-date";
	private JComboBox<Metrics> metrics;
	private ButtonGroup radioGroup;
	private JRadioButton afterChoice, beforeChoice, rangeChoice;
	private TimeInput after;
	private TimeInput before;
	private TimeInput start;
	private TimeInput end;

	public SnapshotQueryPage() {
		super();
		ResourceBundle resources = MeasurementGUI.instance().getResources();
		MeasurementServer server = MeasurementGUI.instance().getServer();
		

		setDescription("Query snapshots");
		setIcon(Images.ICON_SNAPSHOT);
		setLayout(new MigLayout("ins 10"));
		
		add(new JLabel(resources.getString(SnapshotPlugin.PARAM_METRICS)));
		metrics = new NamedComboBox<Metrics>(server.getMetrices());
		add(metrics, "wrap");

		radioGroup = new ButtonGroup();
		afterChoice = new JRadioButton("Snapshots taken after");
		add(afterChoice);
		add(after = new TimeInput(), "wrap");
		radioGroup.add(afterChoice);
		afterChoice.setSelected(true);

		beforeChoice = new JRadioButton("Snapshots taken before");
		add(beforeChoice);
		add(before = new TimeInput(), "wrap");
		radioGroup.add(beforeChoice);

		rangeChoice = new JRadioButton("Snapshots taken between");
		add(rangeChoice);
		add(start = new TimeInput(), "wrap");
		add(new JLabel("and:"));
		add(end = new TimeInput());
		radioGroup.add(rangeChoice);

	}

	@Override
	public boolean verifyUserInputs() {
		// logger.debug("verifying " + this);
		return true;
	}

	@Override
	public Object getUserInput(String key) {
		if (key.equals(PARAM_METRICS)) {
			return metrics.getSelectedItem();
		}
		if (afterChoice.isSelected()) {
			switch (key) {
			case PARAM_START_DATE:
				return after.getTimeInput();
			default:
				return null;
			}
		} else if (beforeChoice.isSelected()) {
			switch (key) {
			case PARAM_END_DATE:
				return before.getTimeInput();
			default:
				return null;
			}
		} else if (rangeChoice.isSelected()) {
			switch (key) {
			case PARAM_START_DATE:
				return start.getTimeInput();
			case PARAM_END_DATE:
				return end.getTimeInput();
			default:
				return null;
			}
		} else {
			return null;
		}
	}

}
