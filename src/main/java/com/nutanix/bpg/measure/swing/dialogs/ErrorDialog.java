package com.nutanix.bpg.measure.swing.dialogs;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * show error with optional stack trace.
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class ErrorDialog extends JDialog {
	private JLabel label;
	private JButton details;
	private JScrollPane stackTrace;
	private JTextArea stacktraceArea;
	
	public ErrorDialog() {
		super();
		setTitle("Error");
		setContentPane(getContentPane());
	}
	
	@Override
	public Container getContentPane() {
		JPanel container = new JPanel();
		container.add(label   = new JLabel());
		container.add(details = new JButton());
		stacktraceArea = new JTextArea();
		stacktraceArea.setEditable(false);
		container.add(stackTrace = new JScrollPane(stacktraceArea));
		
		details.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (stackTrace.isVisible()) {
					details.setText("Show error details");
					stackTrace.setVisible(false);
				} else {
					details.setText("Hide error details");
					stackTrace.setVisible(true);
				}
				ErrorDialog.this.revalidate();
				ErrorDialog.this.repaint();
			}
		});
		return container;
	}
	
	/**
	 * displays error with optional statcktrace.
	 * 
	 * @param ex
	 */
	public void showError(Exception ex) {
		String msg = ex.getMessage();
		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));
		stacktraceArea.setText(writer.toString());
		if (msg != null) {
			label.setText(msg);
		} else {
			stackTrace.setVisible(true);
		}
	}
	
}
