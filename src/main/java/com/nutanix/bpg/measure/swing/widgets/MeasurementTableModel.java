package com.nutanix.bpg.measure.swing.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measure.utils.Conversion;

@SuppressWarnings("serial")
public class MeasurementTableModel extends DefaultTableModel {
	private Metrics metrics;
	private List<Measurement> rows;
	private static Logger logger = LoggerFactory.getLogger(MeasurementTableModel.class);
	
	public MeasurementTableModel(Metrics m, List<Measurement> data) {
		super();
		metrics = m;
		rows = new ArrayList<Measurement>(data);
	}
	
	@Override
	public int getRowCount() {
		int n = rows.size();
		logger.debug("row count:" + n);
		return n;
	}

	@Override
	public int getColumnCount() {
		return metrics.getDimensionCount() + 3;
	}

	@Override
	public String getColumnName(int columnIndex) {
		String name = "";
		switch (columnIndex) {
		case 0: name = "Column 1"; break;
		case 1: name = "Column 2"; break;
		case 2: name = "Column 3"; break;
		default:
			name = metrics.getDimension(columnIndex-3).getName();
		}
		logger.debug("getColumnName " + columnIndex + ":"+ name);
		return name;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> cls = Conversion.resolveTypeByName(
				getDimension(columnIndex).getJavaTypeName());
		logger.debug("getColumnClass " + columnIndex + ":"+ cls);
		return cls;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		logger.debug("isCellEditable? " + rowIndex + "," + columnIndex);;
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		logger.debug("get value at " + rowIndex + "," + columnIndex);;
		MetricsDimension dim = getDimension(columnIndex);
		return rows.get(rowIndex).getValue(dim.getName());
	}


	
	public MetricsDimension getDimension(int columnnIndex) {
		int i = columnnIndex - 3;
		return metrics.getDimension(i);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}
	
}
