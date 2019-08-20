package com.nutanix.bpg.measure.swing.actions;

import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.html.HTMLMaker;
import com.nutanix.bpg.measure.html.HTMLPane;
import com.nutanix.bpg.measure.model.Named;
import com.nutanix.bpg.measure.swing.MeasurementGUI;

/**
 * renders an element as a HTML and shows it in a tab .
 * 
 * @param obj
 * @throws Exception
 */

public class ShowModelElement extends SwingWorker<Object[], Void> {
	private Named obj;
	private Logger logger = LoggerFactory.getLogger(ShowModelElement.class);
	
	public ShowModelElement(Named obj) {
		this.obj = obj;
	}
	
	@Override
	public Object[] doInBackground() {
		logger.debug("create HTML for " + obj);
		HTMLMaker builder = new HTMLMaker();
		try {
			String htmlString = builder.generateHTML(obj);
			return new Object[] { obj, htmlString };
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void done() {
		try {
			Object[] objs = get();
			if (objs == null)
				return;
			Named named = (Named) objs[0];
			String htmlString = (String) objs[1];
			JTabbedPane tabs = MeasurementGUI.instance().getTab();
			HTMLPane html = new HTMLPane();
			html.render(htmlString);
            tabs.addTab(named.getName(), html);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}