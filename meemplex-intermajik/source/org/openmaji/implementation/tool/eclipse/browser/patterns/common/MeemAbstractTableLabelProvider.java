/*
 * @(#)MeemAbstractTableLabelProvider.java
 * Created on 27/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.openmaji.implementation.tool.eclipse.client.presentation.IconExtractor;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.system.meempool.metadata.Abstract;
import org.openmaji.system.presentation.InterMajik;



/**
 * <code>MeemAbstractTableLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemAbstractTableLabelProvider extends TableLabelProviderAdaptor {
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_OVERVIEW = 1;
	public final static int COLUMN_VERSION = 2;
	public final static int COLUMN_COMPANY = 3;
	public final static int COLUMN_COPYRIGHT = 4;
	public final static int COLUMN_AUTHOR = 5;

	/**
	 * Constructs an instance of <code>MeemAbstractTableLabelProvider</code>.
	 * <p>
	 */
	public MeemAbstractTableLabelProvider() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if(!(element instanceof MeemNode)) return null;
		MeemNode node = (MeemNode)element;
		Image image = null;
		switch(columnIndex) {
			case COLUMN_NAME:
			image = IconExtractor.extractSmall(node.getProxy());
			break;
		}
		return image;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		Abstract meemAbstract = null;
		Node node = null;
		if (element instanceof MeemNode) {
			MeemNode meemNode = (MeemNode)element;
			meemAbstract = (Abstract) meemNode.getProxy().getVariableMapProxy().get(InterMajik.ABSTRACT_KEY);
			node = meemNode;
		} else 
		if (element instanceof Node) {
			node = (Node) element;
		}
		
		if(meemAbstract == null) {
			meemAbstract = MeemDocumentationAuthor.DEFAULT_ABSTRACT;
		}

		String text = "";
		switch(columnIndex) {
			case COLUMN_NAME:
			if(meemAbstract.getName().length() == 0)
			text = node.getText();
			else
			text = meemAbstract.getName();
			break;
			
			case COLUMN_OVERVIEW:
			text = meemAbstract.getOverview();
			break;

			case COLUMN_VERSION:
			text = meemAbstract.getVersion();
			break;
						
			case COLUMN_COMPANY:
			text = meemAbstract.getCompany();
			break;
			
			case COLUMN_COPYRIGHT:
			text = meemAbstract.getCopyright();
			break;

			case COLUMN_AUTHOR:
			text = meemAbstract.getAuthor();
			break;
		}
		return text;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ITableColumnProvider#provideColumns(org.eclipse.swt.widgets.Table)
	 */
	public void provideColumns(Table table) {
		TableColumn 
		column = new TableColumn(table, SWT.LEFT, COLUMN_NAME);
		column.setText("Name");
		column.setWidth(150);
		column = new TableColumn(table, SWT.LEFT, COLUMN_OVERVIEW);
		column.setText("Overview");
		column.setWidth(180);
		column = new TableColumn(table, SWT.LEFT, COLUMN_VERSION);
		column.setText("Version");
		column.setWidth(40);
		
		column = new TableColumn(table, SWT.LEFT, COLUMN_COMPANY);
		column.setText("Company");
		column.setWidth(50);

		column = new TableColumn(table, SWT.LEFT, COLUMN_COPYRIGHT);
		column.setText("Copyright");
		column.setWidth(50);

		column = new TableColumn(table, SWT.LEFT, COLUMN_AUTHOR);
		column.setText("Author");
		column.setWidth(50);
	}
}
