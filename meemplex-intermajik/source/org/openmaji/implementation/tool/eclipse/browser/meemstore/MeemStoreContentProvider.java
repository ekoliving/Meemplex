/*
 * @(#)MeemSpaceContentProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemstore;

import java.util.*;


import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.util.MeemPathComparator;
import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.space.meemstore.MeemStoreClient;


/** 
 * @author mg
 * Created on 20/01/2003
 */
public class MeemStoreContentProvider implements IStructuredContentProvider {

	private Set meemPaths;
	private MeemStoreBrowserClient meemStoreBrowserClient;
	private ListViewer viewer;
	private boolean refreshQueued = false;

	private final Reference referenceStore;
	private Meem meemStoreMeem = null;

	public MeemStoreContentProvider() {
		meemPaths = new TreeSet(new MeemPathComparator());

		meemStoreBrowserClient = new MeemStoreBrowserClient(meemPaths);

		meemStoreBrowserClient.setParent(this);

		referenceStore = Reference.spi.create("meemStoreClient", SecurityManager.getInstance().getGateway().getTargetFor(meemStoreBrowserClient, MeemStoreClient.class), true, null);
	}

	/**
	 * This returns all the root elements. In the case of meemstore, all elements are root elements.
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement == meemStoreMeem)
			return meemPaths.toArray();
		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		meemPaths.clear();

		if (meemStoreMeem != null)
		{
			meemStoreMeem.removeOutboundReference(referenceStore);

			meemStoreMeem = null;
		}

		viewer = null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof ListViewer) {
			this.viewer = (ListViewer) viewer;
			((ListViewer)viewer).setComparer(new MeemComparer());
		}

		if (newInput instanceof Meem) {
			//need to register with meemstore here
			meemStoreMeem = (Meem) newInput;
			meemStoreMeem.addOutboundReference(referenceStore, false);
		}
	}

	public void update() {
		if (viewer != null && !refreshQueued) {
			refreshQueued = true;
			Display display = viewer.getControl().getDisplay();
			SWTClientSynchronizer.get(display).execute(new Runnable() {
				public void run() {
					viewer.refresh(false);
					refreshQueued = false;
				}
			});
		}
	}

	private class MeemComparer implements IElementComparer {
		/**
		 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
		 */
		public boolean equals(Object a, Object b) {
			// TODO[peter] Verify that this is unnecessary as facets already support equals directly
//			if ((a instanceof Meem) && (b instanceof Meem)) {
//				Meem meemA = (Meem) MeemUtility.getTarget((Meem)a, "meem", Meem.class);
//				Meem meemB = (Meem) MeemUtility.getTarget((Meem)b, "meem", Meem.class);
//
//				return meemA.getMeemPath().equals(meemB.getMeemPath());
//			}				
//			else
				return a.equals(b);
		}

		/**
		 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
		 */
		public int hashCode(Object element) {
			return element.hashCode();
		}
	}
}
