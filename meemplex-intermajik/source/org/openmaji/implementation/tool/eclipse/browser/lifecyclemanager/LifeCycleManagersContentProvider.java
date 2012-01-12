/*
 * @(#)LifeCycleManagersContentProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.lifecyclemanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.meemserver.MeemServer;




/**
  * <p>
  * ...
  * </p>
  * @author  mg
  * @version 1.0
  */
public class LifeCycleManagersContentProvider implements ITreeContentProvider {

	private Map lcms = new HashMap();
	private Viewer viewer;

	public LifeCycleManagersContentProvider() {
		findLCMs();
	}

	private void findLCMs() {
		addLCM(SecurityManager.getInstance().getGateway().getMeem(MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/persistingLifeCycleManager")));
	}

	public void addLCM(Meem meem) {
		MeemPath meemPath = meem.getMeemPath();
		boolean contained = lcms.containsKey(meemPath);

		if (!contained) {
			// make a new client
			LifeCycleManagerClientImpl client = new LifeCycleManagerClientImpl(this, meem);
			lcms.put(meem.getMeemPath(), client);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof LifeCycleManagerClientImpl)
			return ((LifeCycleManagerClientImpl) parentElement).getChildren();

		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof LifeCycleManagerClientImpl) {
			return ((LifeCycleManagerClientImpl) element).hasChildren();
		}
		return false;
	}

	/**
	 * This returns all the root elements. 
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return lcms.values().toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		if (!lcms.isEmpty()) {
			for (Iterator i = lcms.values().iterator(); i.hasNext();) {
				LifeCycleManagerClientImpl client = (LifeCycleManagerClientImpl) i.next();
				client.dispose();
			}

			lcms.clear();
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof TreeViewer) {
			this.viewer = viewer;
		}
	}
	
	public void update() {
		if (viewer != null) {
			Display display = viewer.getControl().getDisplay();
			SWTClientSynchronizer.getDefault().execute(new Runnable() {
				public void run() {
					viewer.refresh();
				}
			});
		}
	}


}