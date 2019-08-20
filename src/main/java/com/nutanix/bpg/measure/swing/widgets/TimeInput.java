package com.nutanix.bpg.measure.swing.widgets;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TimeInput extends JPanel {
	private JSpinner year;
	private JSpinner month;
	private JSpinner day;
	private JSpinner hour;
	private JSpinner minute;
	private JSpinner second;
	public TimeInput() {
		this(0);
	}
	public TimeInput(int labelPosition) {
		super(true);
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		if (labelPosition == SwingConstants.TOP) {
			addLabels("wrap");
		}
		add(year   = makeSpinner(2019, 2000, 2040, 1, 4));
		add(month  = makeSpinner(1, 1, 12, 1, 2));
		add(day    = makeSpinner(1, 1, 31, 1, 2));
		add(hour   = makeSpinner(0, 0, 24, 1, 2));
		add(minute = makeSpinner(0, 0, 60, 1, 2));
		
		if (labelPosition == SwingConstants.BOTTOM) {
			add(second = makeSpinner(0, 0, 60, 1, 2), "wrap");
			addLabels("");
		} else {
			add(second = makeSpinner(0, 0, 60, 1, 2));
		}
	}
	
	private void addLabels(String wrap) {
		add(new JLabel("year"));
		add(new JLabel("month"));
		add(new JLabel("day"));
		add(new JLabel("hour"));
		add(new JLabel("minute"));
		add(new JLabel("second"), wrap);
	}
	
	private JSpinner makeSpinner(int value, int minimum, int maximum, int stepSize, int width) {
		SpinnerNumberModel model = new SpinnerNumberModel(value, minimum, maximum, stepSize);
		JSpinner spinner = new JSpinner(model);
		JFormattedTextField text = ((JSpinner.DefaultEditor) 
				spinner.getEditor()).getTextField();
		text.setColumns(width);
		spinner.repaint();
		return spinner;
	}
	
	/**
	 * gets a date as selected by each spinner.
	 * @return
	 */
	public Date getTimeInput() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, getValue(year));
		cal.set(Calendar.MONTH, getValue(month));
		cal.set(Calendar.DAY_OF_MONTH, getValue(day));
		cal.set(Calendar.HOUR, getValue(hour));
		cal.set(Calendar.MINUTE, getValue(minute));
		cal.set(Calendar.SECOND, getValue(second));
		
		return cal.getTime();
	}
	
	int getValue(JSpinner spinner) {
		return Integer.class.cast(spinner.getValue());
	}

}
