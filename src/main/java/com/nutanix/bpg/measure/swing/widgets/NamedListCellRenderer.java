package com.nutanix.bpg.measure.swing.widgets;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.nutanix.bpg.measure.model.Named;

@SuppressWarnings("serial")
class NamedListCellRenderer<T> extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(JList<?> list, 
			Object value, int index, boolean isSelected, 
			boolean cellHasFocus) {
		if (Named.class.isInstance(value)) {
			value = Named.class.cast(value).getName();
		} 
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}
}