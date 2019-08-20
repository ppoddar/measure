package com.nutanix.bpg.measure.swing.widgets;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ComboWithLabel<T> extends JComponent {
	private JComboBox<T> input;
	private JLabel label;
	
	public ComboWithLabel(String l, T[] catalog) {
		super();
		this.label = new JLabel(l);
		this.input = new JComboBox<T>();
		this.input.setModel(new NamedComboBoxModel<>(catalog));
		this.input.setRenderer(new NamedListCellRenderer<T>());
		this.input.setSelectedIndex(0);
		setLayout(new FlowLayout());
		add(label);
		add(input);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public T getSelectedItem() {
		return (T)input.getSelectedItem();
	}
}
