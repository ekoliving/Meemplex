/*
 * @(#)ComboTreeViewer.java
 * Created on 24/02/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.viewers;

import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

/**
 * <code>ComboTreeViewer</code>.
 * <p>
 * @author Kin Wong
 */
public class ComboTreeViewer extends AbstractTreeViewer {

	/**
	 * This viewer's control.
	 */
	//private Combo combo;
	
	public ComboTreeViewer(Composite parent) {
		this(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}
	
	public ComboTreeViewer(Composite parent, int style) {
		this(new Combo(parent, style));
	}
	
	public ComboTreeViewer(Combo combo) {
		//this.combo = combo;
		hookControl(combo);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#addTreeListener(org.eclipse.swt.widgets.Control, org.eclipse.swt.events.TreeListener)
	 */
	protected void addTreeListener(Control control, TreeListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#doUpdateItem(org.eclipse.swt.widgets.Item, java.lang.Object)
	 */
	protected void doUpdateItem(Item item, Object element) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getChildren(org.eclipse.swt.widgets.Widget)
	 */
	protected Item[] getChildren(Widget widget) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getExpanded(org.eclipse.swt.widgets.Item)
	 */
	protected boolean getExpanded(Item item) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getItemCount(org.eclipse.swt.widgets.Control)
	 */
	protected int getItemCount(Control control) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getItemCount(org.eclipse.swt.widgets.Item)
	 */
	protected int getItemCount(Item item) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getItems(org.eclipse.swt.widgets.Item)
	 */
	protected Item[] getItems(Item item) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getParentItem(org.eclipse.swt.widgets.Item)
	 */
	protected Item getParentItem(Item item) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getSelection(org.eclipse.swt.widgets.Control)
	 */
	protected Item[] getSelection(Control control) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#newItem(org.eclipse.swt.widgets.Widget, int, int)
	 */
	protected Item newItem(Widget parent, int style, int index) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#removeAll(org.eclipse.swt.widgets.Control)
	 */
	protected void removeAll(Control control) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#setExpanded(org.eclipse.swt.widgets.Item, boolean)
	 */
	protected void setExpanded(Item item, boolean expand) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#setSelection(java.util.List)
	 */
	protected void setSelection(List items) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#showItem(org.eclipse.swt.widgets.Item)
	 */
	protected void showItem(Item item) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	public Control getControl() {
		return null;
	}

}
