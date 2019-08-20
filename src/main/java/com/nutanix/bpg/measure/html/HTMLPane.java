package com.nutanix.bpg.measure.html;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

@SuppressWarnings("serial")
public class HTMLPane extends JEditorPane {
	private static HTMLEditorKit kit;
	static {
		kit = new HTMLEditorKit();
	}

	public HTMLPane() {
		setContentType(kit.getContentType());
		setEditable(false);
		setEditorKit(kit);
	}

	/**
	 * 
	 * @param htmlString
	 */
	public void render(String htmlString) throws Exception {
		Document doc = kit.createDefaultDocument();
		setDocument(doc);
		setText(htmlString);
		repaint();
	}

}

/*
 * private static void addStyle(HTMLEditorKit ed) { // add some styles to the
 * html StyleSheet styleSheet = ed.getStyleSheet();
 * styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
 * styleSheet.addRule("h1 {color: blue;}");
 * styleSheet.addRule("h2 {color: #ff0000;}"); styleSheet.
 * addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }"
 * ); }
 * 
 */
