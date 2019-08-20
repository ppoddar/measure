package com.nutanix.bpg.measure.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.utils.Conversion;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public abstract class Wizard extends JDialog {
	private LinkedList<WizardPage> pages = new LinkedList<>();
	private JButton cancel;
	private JButton finish;
	private JButton next;
	private JButton prev;
	private int response = JOptionPane.CANCEL_OPTION;
	
	private static final Logger logger = LoggerFactory.getLogger(Wizard.class);
	WizardPage current;
	JComponent content;
	JPanel buttonBar;
	
	public Wizard(JFrame parent, WizardPage...pages) {
		super(parent, true);
		this.setLocationRelativeTo(parent);
		init(pages);
	}
	
	private void init(WizardPage...pages) {
		if (pages == null || pages.length == 0) {
			throw new IllegalArgumentException("no page");
		}
		for (WizardPage page : pages) {
			addPage(page);
		}
		content = new JPanel();
		content.setOpaque(true);
		content.setLayout(new BorderLayout());
		
		buttonBar = createButtonBar();
		
		content.add(buttonBar, BorderLayout.SOUTH);
		setContentPane(content);
		
		//setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
	}
	
	/**
	 * creates buttons to navigate across the pages
	 * @param single
	 * @return
	 */
	
	JPanel createButtonBar() {
		JPanel buttons = new JPanel(true);
		buttons.setLayout(new MigLayout());
		buttons.add(new JSeparator(), "wrap");
		next   = new JButton("Next");
		prev   = new JButton("Prev");
		finish = new JButton("Finish");
		cancel = new JButton("Cancel");
		
		if (getPageCount() == 1) {
			finish.setText("OK");
			buttons.add(finish);
			buttons.add(cancel);
		} else {
			buttons.add(next);
			buttons.add(prev);
			buttons.add(finish);
			buttons.add(cancel);
		}
		//content.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		content.add(buttons, BorderLayout.SOUTH);
		setContentPane(content);
		pack();
		
		
		
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("cancel " + Wizard.this);
				Wizard.this.setVisible(false);
				response = JOptionPane.CANCEL_OPTION;
			}
		});
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onNext();
			}
		});
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPrevious();
			}
		});
		
		finish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (onComplete()) {
					response = JOptionPane.OK_OPTION;
					Wizard.this.setVisible(false);
				}
			}
		});
		return buttons;
	}
	
	/**
	 * actions when finish button is pressed
	 */
	boolean onComplete() {
		logger.debug(Wizard.this + ".onComplete() called. Collecting user inputs:"
				+ " from " + getPageCount() + " wizard pages");
		for (WizardPage page : pages) {
			if (!page.verifyUserInputs()) {
				logger.warn("page " + page + " not verified");
				return false;
			}
		}
		return true;
	}
	
	
	void addPage(WizardPage page) {
		pages.add(page);
	}
	
	protected <T extends WizardPage> T getPage(Class<T> t) {
		for (WizardPage p : pages) {
			if (t.isInstance(p)) {
				return t.cast(p);
			}
		}
		return null;
	}
	/**
	 * Display wizard page.
	 * @param i
	 */
	public void showPage(int i) {
		WizardPage page = null;
		try {
			page = pages.get(i);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new InternalError("wizard page naviagation error"
			+ this + " has " + getPageCount() + " pages"
			+ " but trying to show  page " + i);
		}
		logger.debug(this.getName() + " showing page " + i + ":" + page.getName()
		+ " page has " + page.getComponentCount() + " components");
	    content.removeAll();
	    content.add(page.getHeader(), BorderLayout.NORTH);
	    content.add(page, BorderLayout.CENTER);
	    content.add(buttonBar, BorderLayout.SOUTH);
	    setButtonState();
	    pack();
	    revalidate();
	    repaint();
	    
	    current = page;
		
	}
	
	/**
	 * 
	 */
	void onNext() {
		logger.debug("next " + Wizard.this);
		if (current.verifyUserInputs()) {
			showPage(pages.indexOf(current)+1);
		} else {
			logger.warn("all user inputs have not been set "
					+ " on cuurent " + current);
		}
	}
	
	void onPrevious() {
		showPage(pages.indexOf(current)-1);
	}
	
	/**
	 * sets state of navigation buttons.
	 */
	void setButtonState() {
		int i = pages.indexOf(current);
		next.setEnabled(i < getPageCount()-1);
		prev.setEnabled(i > 0);
	}
	
	/**
	 * opens this dialog
	 * @return {@link JOptionPane#OK_OPTION} or
	 * {@link JOptionPane#CANCEL_OPTION} as the
	 * case may be.
	 */
	public int openDialog() {
		showPage(0);
		super.setVisible(true);
		return response;
	}
	
	
	
	public int getResponse() {
		return response;
	}
	
	
	
	/**
	 * gets number of pages in this receiver.
	 * @return
	 */
	public int getPageCount() {
		return pages.size();
	}
	
	@Override
	public void setVisible(boolean show) {
		if (show) showPage(0);
		super.setVisible(show);
	}
	
	public String toString() {
		return "wizard:" + getName();
	}
	
	/**
	 * gets a property converted to given target type, if possible.
	 * @param key property key
	 * @param t target type
	 * @return
	 */
	public <T> T getUserInput(String key, Class<T> t) {
		Object value = null;
		for (WizardPage page : pages) {
			value = page.getUserInput(key);
			if (value != null) break;
		}
		if (value == null) {
			throw new RuntimeException("no property [" + key + "] in " + this);
		}
		return Conversion.convert(value, t);
		
	}
	abstract public void performAction();	
	
}
