package com.nutanix.bpg.measure.swing;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class MenuBuilder {
	public static JMenuBar build() {
		JMenuBar menuBar = new JMenuBar();
		JMenu databaseMenu = new JMenu("Database");
		menuBar.add(databaseMenu);
		addMenuItem(databaseMenu, Actions.DATABASE_NEW);
		
		JMenu snapshotMenu = new JMenu("Snapshot");
		menuBar.add(snapshotMenu);
		addMenuItem(snapshotMenu, Actions.SNAPSHOT_CURRENT);
		addMenuItem(snapshotMenu, Actions.SNAPSHOT_NEW);
		addMenuItem(snapshotMenu, Actions.SNAPSHOT_SHOW);

		return menuBar;
	}
	
	static void addMenuItem(JMenu menu, Action action) {
		JMenuItem menuItem = new JMenuItem();
		menuItem.setAction(action);
		menu.add(menuItem);
	}
	
	
}
	/**
	 * create a menu and associate action with menu items.
	 * 
	 * @param rsrc a YAML classpath resource
	 * @return a MenuBar
	 * @throws Exception
	 */
//	public static JMenuBar createMenuBar(String rsrc) throws Exception {
//		JMenuBar menuBar = new JMenuBar();	
//		JsonNode menuDescriptor = getResource(rsrc);
//		logger.info("menu has " + menuDescriptor.size() + " top-level menus");
//		for (JsonNode menuNode : menuDescriptor) {
//			JMenu menu = createMenu(menuNode);
//			menuBar.add(menu);
//		}
//		return menuBar;
//	}

	/**
	 * Create a Menu and its submenus, if any.
	 * @param json
	 * @return
	 */
//	static JMenu createMenu(JsonNode json) {
//		JMenu menu = new JMenu();
//		assertProperty(json, "name");
//		String name = json.get("name").asText();
//		menu.setText(name);
//		
//		
//		JsonNode subMenus = json.path("sub-menus");
//		logger.info("menu [" + menu.getText() + "] has " + subMenus.size() + " sub-menus");
//		for (JsonNode sub : subMenus) {
//		    JMenu subMenu = createMenu(sub);
//		    menu.add(subMenu);
//		}
//		if (json.has("action")) {
//			JsonNode actionNode = json.path("action");
//			JMenuItem menuItem = createActionMenuItem(actionNode);
//			menu.add(menuItem);
//			setMenuAction(menu, actionNode);
//		}
//		
//		return menu;
//	}
//	
//	static JMenuItem createActionMenuItem(JsonNode actionNode) {
//		JMenuItem menuItem = new JMenuItem();
//		assertProperty(actionNode, "name");
//		String actionName = actionNode.path("name").asText();
//		menuItem.setText(actionName);
//		if (actionNode.has("display-name")) {
//			menuItem.setText(actionNode.get("display-name").asText());
//		}
//		logger.info("menu [" + menuItem.getText() + "] action=" + actionName);
//		Action action = Actions.getAction(actionName);
//		menuItem.setAction(action);
//	
//		return menuItem;
//	}
//	
//	static void setMenuAction(JMenu item, JsonNode actionNode) {
//		assertProperty(actionNode, "name");
//		String actionName = actionNode.path("name").asText();
//		//item.setText(actionName);
//		if (actionNode.has("display-name")) {
//			//item.setText(actionNode.get("display-name").asText());
//		}
//		logger.info("menu [" + item.getText() + "] action=" + actionName);
//		Action action = Actions.getAction(actionName);
//		item.setAction(action);
//	}
//
//	
//	/**
//	 * parses the given classpath resource to JSON
//	 * @param rsrc
//	 * @return
//	 * @throws Exception
//	 */
//	static JsonNode getResource(String rsrc) throws Exception{
//		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//		InputStream in = Thread.currentThread()
//				.getContextClassLoader()
//				.getResourceAsStream(rsrc);
//		if (in == null) {
//			throw new IllegalArgumentException("Classpath resource [" + rsrc + "] not found");
//		}
//		JsonNode menuDescriptor = mapper.readTree(in);
//		return menuDescriptor;
//	}
//	
//	
//	static void assertProperty(JsonNode json, String name) {
//		if (!json.has(name)) {
//			throw new RuntimeException("missing json property [" + name + "]");
//		}
//	}
//
//}
//
//
//JMenu fileMenu = new JMenu("File");
//menuBar.add(fileMenu);
//
//JMenu fileSubMenu = new JMenu("New...");
//JMenuItem menuItem = new JMenuItem(Actions.NEW_PROJECT);
//fileSubMenu.add(menuItem);
//fileMenu.add(fileSubMenu);
//
//JMenu collectMenu = new JMenu("Measure");

