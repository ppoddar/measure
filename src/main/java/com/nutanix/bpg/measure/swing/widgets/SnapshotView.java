package com.nutanix.bpg.measure.swing.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.swing.MeasurementGUI;

/**
 * SnapshotView shows list of snapshot in upper pane
 * and details of a snapshot in a table in lower pane.
 * 
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public class SnapshotView extends JSplitPane {
	private final JComponent list;
	private JScrollPane lowerScrollPane;
	private static Logger logger = LoggerFactory.getLogger(SnapshotView.class);
	
	public SnapshotView(Metrics m, List<Snapshot> snapshots) {
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		list = new JPanel();
		list.setBackground(Color.WHITE);
		lowerScrollPane = new JScrollPane();
		lowerScrollPane.setPreferredSize(new Dimension(200, 200));
		lowerScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		setLeftComponent(list);
		setRightComponent(lowerScrollPane);
		// populate list with Snapshot essential info
		for (Snapshot sn : snapshots) {
			SnapshotLineView view = new SnapshotLineView(sn, m);
			int N = view.getComponentCount();
			MouseListener l = new ShowMeasurement(sn, m);
			for (int i = 0; i < N; i++) {
				view.getComponent(i).addMouseListener(l);
			}
			list.add(view);
		}
	}

	class ShowMeasurement extends MouseAdapter {
		private final Snapshot sn;
		private final Metrics metrics;

		ShowMeasurement(Snapshot sn, Metrics m) {
			this.sn = sn;
			this.metrics = m;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			logger.debug("mouse clicked on " + sn);
			final MeasurementServer api = MeasurementGUI.instance().getServer();
			SwingWorker<List<Measurement>, Void> worker = new SwingWorker<List<Measurement>, Void>() {
				@Override
				protected List<Measurement> doInBackground() throws Exception {
					return api.getMeasurements(sn, metrics);
				}

				protected void done() {
					try {
						List<Measurement> list = get();
						logger.debug("received " + list.size() + " measurements");
						DefaultTableModel model = new DefaultTableModel();
						boolean first = true;
						for (Measurement m : list) {
							logger.debug("add row " + m);
							model.addRow(toData(m));
							if (first) {
								addColumns(m, model);
								first = false;
							}
						}
						JTable table = new JTable(model);
						//table.setFillsViewportHeight(true);
						JViewport viewPort = lowerScrollPane.getViewport();
						viewPort.removeAll();
						viewPort.add(table);
						SnapshotView.this.repaint();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			};
			worker.execute();
		}
		
		Object[] toData(Measurement m) {
			int M = m.getMetrics().getDimensionCount();
			int K = 4;
			int N =  M + K;
			Object[] data = new Object[N];
			data[0] = m.getId();
			data[1] = m.getStartTime();
			data[2] = m.getEndTime();
			data[3] = m.getMetrics().getName();
			for (int j = 0; j < M; j++) {
				MetricsDimension d = m.getMetrics().getDimension(j);
				data[j+K] = m.getValue(d.getName());
			}
			return data;
		}
		
		void addColumns(Measurement m, DefaultTableModel model) {
			int M = m.getMetrics().getDimensionCount();
			int K = 4;
			int N =  M + K;
			Object[] data = new Object[N];
			model.addColumn("ID");
			model.addColumn("Start");
			model.addColumn("End");
			model.addColumn("Metrics");
			for (int j = 0; j < M; j++) {
				MetricsDimension d = m.getMetrics().getDimension(j);
				model.addColumn(d.getName());
			}
		}

		
	}
}
