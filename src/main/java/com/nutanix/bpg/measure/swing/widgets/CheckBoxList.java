package com.nutanix.bpg.measure.swing.widgets;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * list with check boxes.
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class CheckBoxList extends JList<JCheckBox> {
	DefaultListModel<JCheckBox> model = new DefaultListModel<>();
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	/**
	 * 
	 */
	public CheckBoxList() {
		setCellRenderer(new CellRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setModel(model);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index != -1) {
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
			}
		});
	}
	
	/**
	 * Adds an item to underlying model.
	 * The actual item added is a JCheckBox
	 * with given string.
	 * @param item
	 */
	public void addItems(String item) {
		model.addElement(new JCheckBox(item));
	}
	
	public List<String> getSelectedElements() {
		List<String> selected = new ArrayList<>();
		int N = getModel().getSize();
		for (int i = 0; i < N; i++) {
			JCheckBox item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selected.add(item.getText());
			}
		}
		return selected;
	}

	protected class CellRenderer implements ListCellRenderer<JCheckBox> {
		@Override
		public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index,
				boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox)value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}

	public static void main(String[] args) throws Exception {
		CheckBoxList list = new CheckBoxList();
		DefaultListModel<JCheckBox> model = new DefaultListModel<>();
		model.addElement(new JCheckBox("A1"));
		model.addElement(new JCheckBox("A2"));
		model.addElement(new JCheckBox("A3"));
		list.setModel(model);
		JFrame frame = new JFrame("test");
		frame.setPreferredSize(new Dimension(100, 100));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(list);
		frame.pack();
		frame.setVisible(true);
	}

}
