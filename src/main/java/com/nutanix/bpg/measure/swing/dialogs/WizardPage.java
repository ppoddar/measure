package com.nutanix.bpg.measure.swing.dialogs;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * A {@link WizardPage} is a step in a {@link Wizard
 * wizard} based workflow.
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public abstract class WizardPage extends JPanel {
	private JComponent header;
	private ImageIcon icon;
	private String description;
	@SuppressWarnings("unused")
	private Wizard wizard;
	
	/*
	 * create an empty page with simple name
	 * of this class.
	 */
	public WizardPage() {
		super(true);
		setName(getClass().getSimpleName());
	}
	
	/**
	 * gets a panel that would be displayed as header 
	 * for the page
	 * @return
	 */
	protected JComponent getHeader() {
		if (header == null) {
			header = new JPanel();
			header.setLayout(new MigLayout());
			header.add(
			new JLabel(getDescription(), getIcon(), JLabel.CENTER));
			header.setMinimumSize(new Dimension(100, 40));
			header.setBackground(Color.WHITE);
		}
		return header;
	}
	
	protected void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}
	
	protected void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	protected void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	public ImageIcon getIcon() {
		return this.icon;
	}
	
	
	public String toString() {
		return "page:" + getName();
	}
	
	public abstract boolean verifyUserInputs();
	
	public abstract Object getUserInput(String key);
}
