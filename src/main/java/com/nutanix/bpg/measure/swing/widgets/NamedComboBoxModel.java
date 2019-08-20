package com.nutanix.bpg.measure.swing.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

public class NamedComboBoxModel<T> 
	implements ComboBoxModel<T> {
	final private List <T> items;
	private T selected;
	
	public NamedComboBoxModel(T[] values) {
		this.items = Arrays.asList(values);
	}
	
	public NamedComboBoxModel(Collection<T> values) {
		this.items = new ArrayList<>(values);
	}
	

	
	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public T getElementAt(int index) {
		return items.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		selected = (T)anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}
	
	@Override
	public void addListDataListener(ListDataListener l) {
		
	}
	@Override
	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

}
