package com.nutanix.bpg.measure.swing.widgets;

import java.util.Collection;

import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class NamedComboBox<T> extends JComboBox<T> {
	public NamedComboBox(Collection<T> items) {
		this.setModel(new NamedComboBoxModel<T>(items));
		this.setRenderer(new NamedListCellRenderer<T>());
		this.setSelectedIndex(0);
	}

}
