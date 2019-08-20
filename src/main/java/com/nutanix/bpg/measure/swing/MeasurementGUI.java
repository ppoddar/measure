package com.nutanix.bpg.measure.swing;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.MeasurementServerImpl;
import com.nutanix.bpg.measure.html.HTMLPane;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.PluginMetadata;
import com.nutanix.bpg.measure.utils.ClasspathUtils;

/**
 * Main entry point for Swing based Database Health Checker Tool
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class MeasurementGUI extends JFrame implements WindowListener {
	private Navigator   navigator;
	private JTabbedPane tabs;
	private ResourceBundle uiProperties;

	private static String TITLE = "Database Measurement";
	private static int MAIN_WIDTH  = 1200;
	private static int MAIN_HEIGHT = 700;
	private static float DIVIDER_FACTOR = 0.2f;

	private static MeasurementServer server;
	private static MeasurementGUI singleton;

	private static final Logger logger = LoggerFactory.getLogger(MeasurementGUI.class);

	public static MeasurementGUI instance() {
		if (singleton == null) {
			try {
				singleton = new MeasurementGUI();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return singleton;
	}

	public static void main(String[] args) throws Exception {
		// showSplash();
		logger.info("starting Measurement Service...");
		String rsrc = "bpg.properties";
		Properties serverProperties = new Properties();
		InputStream in = ClasspathUtils.getInputStream(rsrc);
		serverProperties.load(in);
		serverProperties.store(System.out, "server properties");
		server = MeasurementServerImpl.init(serverProperties);

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					MeasurementGUI.instance().createAndShowGUI();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private MeasurementGUI() {
		uiProperties = ResourceBundle.getBundle("bpg-ui");
	}

	private void createAndShowGUI() throws Exception {
		createMainFrame();
		JMenuBar menubar = MenuBuilder.build();
		setJMenuBar(menubar);
		JPanel content = new JPanel(true);
		content.setLayout(new BorderLayout());

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		navigator = createNavigator();
		tabs      = createTabs();

		splitPane.setLeftComponent(new JScrollPane(navigator));
		splitPane.setRightComponent(new JScrollPane(tabs));

		content.add(splitPane, BorderLayout.CENTER);
		Dimension size = new Dimension(MAIN_WIDTH, MAIN_HEIGHT);
		content.setPreferredSize(size);
		content.setMinimumSize(size);
		setContentPane(content);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);

		splitPane.setDividerLocation(DIVIDER_FACTOR);
		splitPane.resetToPreferredSizes();
	}

	public MeasurementServer getServer() {
		return server;
	}
	public Navigator getNavigator() {
		return navigator;
	}
	
	public JTabbedPane getTab() {
		return tabs;
	}
	
	public ResourceBundle getResources() {
		return uiProperties;
	}
	
	private Navigator createNavigator() {
		JPanel panel = new JPanel(true);
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		Navigator navigator = new Navigator();
		Dimension dimLeft = new Dimension((int) (DIVIDER_FACTOR) * MAIN_WIDTH, MAIN_HEIGHT);
		navigator.setPreferredSize(dimLeft);
		navigator.setMinimumSize(dimLeft);
		panel.add(navigator);

		navigator.addCatalog("metrics", Metrics.class, server.getMetrices());
		navigator.addCatalog("plugins", PluginMetadata.class, server.getPlugins());
		navigator.addCatalog("databases", Database.class, server.getDatabases());
		return navigator;
	}

	private JTabbedPane createTabs() {
		tabs = new JTabbedPane(JTabbedPane.TOP);
		Dimension dimRight = new Dimension((int) (1.0 - DIVIDER_FACTOR) * MAIN_WIDTH, MAIN_HEIGHT);
		tabs.setMinimumSize(dimRight);
		tabs.setPreferredSize(dimRight);
		return tabs;
	}

	private void createMainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Images.ERA_LOGO.getImage());
		setSize(MAIN_WIDTH, MAIN_HEIGHT);
		setTitle(TITLE);
		addWindowListener(this);
	}


	static void showSplash() {

		try {
			SplashScreen splash = SplashScreen.getSplashScreen();
			if (splash == null) {
				logger.warn("splashscrren is null");
				return;
			}
			Graphics2D g = splash.createGraphics();
			if (g == null) {
				logger.warn("splashscrren graphics is null");
				return;
			}
			g.setComposite(AlphaComposite.Clear);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int response = JOptionPane.showConfirmDialog(null, "close");
		if (response == JOptionPane.OK_OPTION) {
			this.dispose();
			System.exit(0);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		int state = getExtendedState();
		state = state & ~JFrame.ICONIFIED;
		setExtendedState(state);
		setVisible(true);
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	

}
