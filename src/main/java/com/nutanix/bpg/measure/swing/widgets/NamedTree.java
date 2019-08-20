package com.nutanix.bpg.measure.swing.widgets;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measure.model.Named;
import com.nutanix.bpg.measure.swing.MeasurementGUI;
import com.nutanix.bpg.measure.swing.actions.ShowModelElement;

/**
 * a subtree shows named items of a single tree. Notifies when a selection
 * changes
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class NamedTree extends JTree implements TreeSelectionListener {
	DefaultMutableTreeNode root;

	/**
	 * a named tree has a root with given name
	 * 
	 * @param name
	 */
	public NamedTree() {
		super();
		setBackground(Color.WHITE);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addTreeSelectionListener(this);
	}

	/**
	 * 
	 * @param items
	 */
	public void addCatalog(String name, Collection<?> items) {
		root = new DefaultMutableTreeNode();
		root.setAllowsChildren(true);
		root.setUserObject(name);
		this.setRootVisible(true);
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);
		for (Object item : items) {
			addNode(root, item);
		}
	}

	/**
	 * adds a single node to this tree.
	 * 
	 * @param parent
	 * @param item
	 */
	public DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent, Object item) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(item);
		int idx = parent.getChildCount();
		getModel().insertNodeInto(newNode, parent, idx);
		TreePath path = new TreePath(newNode.getPath());
		this.scrollPathToVisible(path);

		if (Metrics.class.isInstance(item)) {
			List<MetricsDimension> dims = Metrics.class.cast(item).getDimensions();
			for (MetricsDimension dim : dims) {
				addNode(newNode, dim);
			}
		}
		this.collapsePath(path);
		return newNode;
	}

	public DefaultTreeModel getModel() {
		return (DefaultTreeModel) super.getModel();
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) 
				e.getPath().getLastPathComponent();
		if (selected == null) return;

		final Object userObj = selected.getUserObject();
		clearSelection();
		if (!Named.class.isInstance(userObj)) return;
		
		new ShowModelElement(Named.class.cast(userObj))
			.execute();
	}


	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Object userObject = DefaultMutableTreeNode.class
				.cast(value).getUserObject();
		if (Named.class.isInstance(userObject)) {
			return Named.class.cast(userObject).getName();
		} else {
			return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
		}
	}

}