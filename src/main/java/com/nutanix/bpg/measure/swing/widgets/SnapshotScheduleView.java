package com.nutanix.bpg.measure.swing.widgets;

import java.util.concurrent.TimeUnit;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.nutanix.bpg.measure.model.SnapshotSchedule;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class SnapshotScheduleView extends JPanel {
	private JSpinner count;
	private JSpinner delay;
	private JComboBox<TimeUnit> unit;
	
	public SnapshotScheduleView() {
		setLayout(new MigLayout());
		
		SpinnerNumberModel countModel = new SpinnerNumberModel(2, 2, 100, 1);
		add(count = new JSpinner(countModel));
		SpinnerNumberModel delayModel = new SpinnerNumberModel(1, 1, 60, 1);
		add(delay = new JSpinner(delayModel));
		TimeUnit[] units = new TimeUnit[] { 
			TimeUnit.MINUTES,
			TimeUnit.SECONDS};
		add(unit = new JComboBox<TimeUnit>(units), "wrap");
		
		add(new JLabel("count"));
		add(new JLabel("interval"), "wrap");
	}
	
	public SnapshotSchedule getValue() {
		SnapshotSchedule schedule = new SnapshotSchedule(
				(Integer)count.getValue(),
				(Integer)delay.getValue(),
				(TimeUnit)unit.getSelectedItem());
		return schedule;
	}

}
