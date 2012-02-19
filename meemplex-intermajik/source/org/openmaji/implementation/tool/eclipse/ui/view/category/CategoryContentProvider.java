/*
 * @(#)SpaceContentProvider.java
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

import java.util.TreeMap;
import java.util.Map;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;
import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.space.CategoryEntry;


/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class CategoryContentProvider implements IStructuredContentProvider {

	private Reference reference;
	private Meem meem;
	boolean refreshing = false;
	
	Viewer viewer;

	Map entries = new TreeMap();

	public CategoryContentProvider() {
		//MeemPath categoryClientMeemPath = MeemUtility.createMeemPath(Space.transitory, "CategoryClient");

		CategoryClient categoryClient = new CategoryClient();
		categoryClient.setProvider(this);

		reference = Reference.spi.create("categoryClient", SecurityManager.getInstance().getGateway().getTargetFor(categoryClient, org.openmaji.system.space.CategoryClient.class), true, null);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		entries.clear();

		if (meem != null) 
		{
			meem.removeOutboundReference(reference);
		}
	}

	/**
	 * This is where the dependency upon a category is added and removed. <br>
	 * The input should be a category meem
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, final Object newInput) {
		this.viewer = viewer;

		if (meem != null)
		{
			entries.clear();
			// remove reference
			meem.removeOutboundReference(reference);
			//System.out.println("dep removed: " + categoryDependencyTarget.toString());
		}

		if (newInput != null)
		{
			entries.clear();
			// set up reference 
			meem = (Meem) newInput;
			meem.addOutboundReference(reference, false);
		}

		update();
	}

	private void update() {
		if (viewer != null) {
			if(!refreshing) {
				refreshing = true;
				Display display = viewer.getControl().getDisplay();
				SWTClientSynchronizer.get(display).execute(new Runnable() {
					public void run() {
						viewer.refresh();
					}
				});
				refreshing = false;
			}
		}
	}

	public void addEntry(CategoryEntry entry) {
		// need to store NamedMeem entries instead of CategoryEntries
		NamedMeem namedMeem = new NamedMeem(entry.getName(), entry.getMeem().getMeemPath());
		entries.put(namedMeem.getName(), namedMeem);
		update();
	}

	public void removeEntry(CategoryEntry entry) {
		entries.remove(entry.getName());
		update();
	}

	public void renameEntry(CategoryEntry oldEntry, CategoryEntry newEntry) {
		entries.remove(oldEntry.getName());
		NamedMeem namedMeem = new NamedMeem(newEntry.getName(), newEntry.getMeem().getMeemPath());
		entries.put(namedMeem.getName(), namedMeem);
		update();
	}

	public NamedMeem[] getElements() {
		return (NamedMeem[]) entries.values().toArray(new NamedMeem[0]);
	}

	public Object[] getElements(Object object) {
		//return (Object[]) entries.values().toArray(new Object[0]);
		//System.out.println("getElements(" + object + ")");
		return new Object[0];
	}

}
