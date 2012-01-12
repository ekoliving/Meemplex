/*
 * @(#)TableLabelProviderAdaptor.java
 * Created on 14/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;


/**
 * <code>TableLabelProviderAdaptor</code>.
 * <p>
 * @author Kin Wong
 */
public class TableLabelProviderAdaptor
	implements ITableColumnProvider, ITableLabelProvider {

		private ArrayList listeners = new ArrayList();
		private Map mapIndexToColumn = new HashMap();
		private ArrayList columns = new ArrayList();

	/**
	 * Constructs an instance of <code>TableLabelProviderAdaptor</code>.
	 * <p>
	 * 
	 */
	public TableLabelProviderAdaptor() {
	}

	protected void addColumn(Column column) {
		columns.add(column);
		fireLabelProviderChanged(null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if(!(element instanceof Node)) return null;
		Integer indexInteger = new Integer(columnIndex);
		Column column = (Column)mapIndexToColumn.get(indexInteger);
		if(column == null) return null;
		return getColumnImage(column, (Node)element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof Node)) return null;
		Integer indexInteger = new Integer(columnIndex);
		Column column = (Column)mapIndexToColumn.get(indexInteger);
		if(column == null) return "";
		return getColumnText(column, (Node)element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Column column, Node node) {
		return column.getImage(node);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Column column, Node node) {
		String text = column.getText(node);
		if(text == null) return "";
		return text;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ITableColumnProvider#provideColumns(org.eclipse.swt.widgets.Table)
	 */
	public void provideColumns(Table table) {
		mapIndexToColumn.clear();
		
		int index = 0;
		for (Iterator it = columns.iterator(); it.hasNext();) {
			Column column = (Column)it.next();
			TableColumn tableColumn = new TableColumn(table, column.getStyle());
			tableColumn.setText(column.getName());
			tableColumn.setWidth(column.getWidth());
			mapIndexToColumn.put(new Integer(index), column);
			
			// next column
			index++;
		}			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
		
	protected void fireLabelProviderChanged(Object elements) {
		if(listeners.isEmpty()) return;
		LabelProviderChangedEvent event = 
			new LabelProviderChangedEvent(this, elements);

		Object[] clients = listeners.toArray();
		for (int i = 0; i < clients.length; i++) {
			((ILabelProviderListener)clients[i]).labelProviderChanged(event);
		}
	}
}
