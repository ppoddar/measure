package com.nutanix.bpg.measure.swing;

import java.net.URL;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Images {
	private static final Logger logger = LoggerFactory.getLogger(Images.class);

	
	public static ImageIcon BLANK_IMAGE   = getImage("images/blank.gif");
	public static ImageIcon ERA_LOGO      = getImage("images/era-icon.png");
	public static ImageIcon ICON_METRICS  = getImage("images/snapshot.png");
	public static ImageIcon ICON_DATABASE = getImage("images/database.gif");
	public static ImageIcon ICON_PLUGINS  = getImage("images/plugin.gif");
	public static ImageIcon ICON_OPENED   = getImage("images/opened.gif");
	public static ImageIcon ICON_CLOSED   = getImage("images/closed.gif");
	public static ImageIcon ICON_SNAPSHOT  = getImage("images/snapshot.png");
	
	public static ImageIcon getImage(String name) {
		//Toolkit.getDefaultToolkit().getI
		URL url = Thread.currentThread()
				.getContextClassLoader()
				.getResource(name);
		if (url != null) {
			logger.debug("loading image from [" + url + "]");
			return new ImageIcon(url);
		} else {
			logger.warn("no image [" + name + "]");
			return BLANK_IMAGE;
		}
	}

}
