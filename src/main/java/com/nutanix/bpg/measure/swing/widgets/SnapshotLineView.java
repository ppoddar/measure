package com.nutanix.bpg.measure.swing.widgets;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Snapshot;

import net.miginfocom.swing.MigLayout;
/**
 * A view of Snapshot essentials.
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class SnapshotLineView extends JPanel {
	private static Logger logger = LoggerFactory.getLogger(SnapshotLineView.class);
	public SnapshotLineView(Snapshot sn, Metrics m) {
		super(true);
		setBackground(Color.WHITE);
		setLayout(new MigLayout());
		add(new JLabel("<html>Snapshot:<b>" + sn.getName() + "</b></html>"), "wrap");
		
		add(new JLabel("start:" + sn.getStartTime()));
		add(new JLabel("end:" + sn.getEndTime()), "wrap");
		add(new JLabel("" + sn.getExpectedMeasurementCount() + " measurements"), "wrap");
	}
}
