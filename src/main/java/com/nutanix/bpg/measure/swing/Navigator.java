package com.nutanix.bpg.measure.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.PluginMetadata;
import com.nutanix.bpg.measure.swing.widgets.NamedTree;

@SuppressWarnings("serial")

/**
 * A tree view of {@link Named} elements in separate type-specifc subtrees.
 * 
 * @author pinaki.poddar
 *
 */
public class Navigator extends JPanel {
	private Map<Class<?>, NamedTree> subtrees;

	private static Class<?>[] classes;
	private static Map<Class<?>, ImageIcon> icons;
	private static Map<Class<?>, String> rootNames;

	static Logger logger = LoggerFactory.getLogger(Navigator.class);

	static {
		classes = new Class<?>[] { Metrics.class, Database.class, PluginMetadata.class };
		icons = new HashMap<>();
		rootNames = new HashMap<>();
		icons.put(Metrics.class, Images.ICON_METRICS);
		icons.put(Database.class, Images.ICON_DATABASE);
		icons.put(PluginMetadata.class, Images.ICON_PLUGINS);
		rootNames.put(Metrics.class, "metrices");
		rootNames.put(Database.class, "databases");
		rootNames.put(PluginMetadata.class, "plugins");

	}

	public Navigator() {
		super();
		setBackground(Color.WHITE);
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 10));
		subtrees = new HashMap<Class<?>, NamedTree>();
		for (Class<?> cls : classes) {
			logger.debug("adding subtree for " + cls.getSimpleName());
			NamedTree subtree = new NamedTree();
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) subtree.getCellRenderer();
			renderer.setOpenIcon(icons.get(cls));
			subtree.setPreferredSize(new Dimension(400, 400));
			subtree.setMaximumSize(new Dimension(400, 400));
			subtrees.put(cls, subtree);
			add(subtree);
		}
	}

	/**
	 * finds a subtree corresponding to given class (or its superclass).
	 * 
	 * @param c class for which subtree to be found
	 * @return null if not found
	 */
	NamedTree findSubTree(Class<?> c) {
		for (Map.Entry<Class<?>, NamedTree> e : subtrees.entrySet()) {
			Class<?> cls = e.getKey();
			if (cls.isAssignableFrom(c)) {
				return e.getValue();
			}
		}
		return null;
	}

	/**
	 * adds collection of named items
	 * 
	 * @param items
	 */
	public <T> void addCatalog(String name, 
			Class<T> cls, 
			Collection<T> items) {
		if (items == null) {
			throw new IllegalArgumentException("null items can not be added to tree");
		}
		if (items.isEmpty()) {
			logger.warn("empty set of items added to sub-tree");
			return;
		}
		NamedTree tree = findSubTree(cls);
		if (tree == null) {
			throw new RuntimeException("no subtree for " + cls.getName());
		}
		tree.addCatalog(name, items);
	}

}
