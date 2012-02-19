/*
 * @(#)CategoryViewer.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.view.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.SunkenFreeformGraphicalRootEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;
import org.openmaji.implementation.tool.eclipse.ui.view.category.model.Category;


/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class CategoryViewer extends StructuredViewer {

	Combo combo;
	List selected = new ArrayList();

	Category category = new Category();
	ScrollingGraphicalViewer viewer;

	Map categories = new TreeMap();

	public CategoryViewer(Composite parent, EditPartFactory editPartFactory) {

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		combo.addSelectionListener(new SelectionListener() {
			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent e) {
				showPage(combo.getText());
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				showPage(combo.getText());
			}

		});
		
		viewer = new ScrollingGraphicalViewer();

		viewer.setEditDomain(new DefaultEditDomain(null));

		viewer.createControl(parent);
			RootEditPart root = new SunkenFreeformGraphicalRootEditPart();
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(editPartFactory);
		viewer.setContents(category);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

	}
		
	public void addDragSourceListener(TransferDragSourceListener listener) {
		//viewer.addDragSourceListener(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	public Control getControl() {
		return viewer.getControl();
	}

	public void addCategory(String name, org.openmaji.meem.Meem category) {
//		boolean firstEntry = categories.isEmpty();
		categories.put(name, category);
		updateCombo();

//		if (firstEntry) {
//			showCategory(0);
//		}
	}

	public void removeCategory(String name) {
		Object entry = categories.remove(name);
		if (entry != null) {
			updateCombo();
		}
	}

	public void renameCategory(String oldName, String newName) {
		Object entry = categories.remove(oldName);
		if (entry != null) {
			categories.put(newName, entry);
			updateCombo();
		}
	}

	private void updateCombo() {
		if (!combo.isDisposed()) { 
			Display display = getControl().getDisplay();
			SWTClientSynchronizer.get(display).execute(new Runnable() {
				public void run() {
					combo.removeAll();
					for (Iterator i = categories.keySet().iterator(); i.hasNext();) {
						String key = (String) i.next();
						combo.add(key);
					}
				}
			});
		}
	}

	public void showCategory(int category) {
		if (category <= combo.getItemCount()) {
			combo.select(category);
			showPage(combo.getText());
		}
	}

	private void removeMeems() {
		category.clear();
	}

	private void showPage(String category) {
		removeMeems();

		setInput(categories.get(category));
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindInputItem(java.lang.Object)
	 */
	protected Widget doFindInputItem(Object element) {
		System.out.println("doFindInputItem(" + element + ")");
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindItem(java.lang.Object)
	 */
	protected Widget doFindItem(Object element) {
		System.out.println("doFindItem(" + element + ")");
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#doUpdateItem(org.eclipse.swt.widgets.Widget, java.lang.Object, boolean)
	 */
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		System.out.println("doUpdateItem(" + item + ", " + element + ")");
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#getSelectionFromWidget()
	 */
	protected List getSelectionFromWidget() {
		return selected;
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#internalRefresh(java.lang.Object)
	 */
	protected void internalRefresh(Object element) {
		//	trash existing meems 
		removeMeems();

		// get list of entries from provider
		NamedMeem[] entries = ((CategoryContentProvider) getContentProvider()).getElements();

		for (int i = 0; i < entries.length; i++) {
			NamedMeem entry = entries[i];
			String name = ((LabelProvider) getLabelProvider()).getText(entry);
			
			Meem meem = new Meem(InterMajikClientProxyFactory.getInstance().locate(entry.getMeemPath()));//, new Point(0,0));
			meem.setName(name);
			category.addChild(category.getChildren().size(), meem);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#reveal(java.lang.Object)
	 */
	public void reveal(Object element) {
		System.out.println("reveal(" + element + ")");
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(java.util.List, boolean)
	 */
	protected void setSelectionToWidget(List l, boolean reveal) {
		//System.out.println("setSelectionToWidget(" + l + ", " + reveal + ")");
	}

	public ScrollingGraphicalViewer getViewer() {
		return viewer;
	}

}
