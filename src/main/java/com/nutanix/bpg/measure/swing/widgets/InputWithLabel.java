package com.nutanix.bpg.measure.swing.widgets;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class InputWithLabel extends JComponent {
	private JTextField input;
	private JLabel label;
	public InputWithLabel(String l) {
		super();
		this.label = new JLabel(l);
		this.input = new JTextField(24);
		setLayout(new FlowLayout());
		add(label);
		add(input);
	}
	
	public String getValue() {
		return input.getText();
	}
}
