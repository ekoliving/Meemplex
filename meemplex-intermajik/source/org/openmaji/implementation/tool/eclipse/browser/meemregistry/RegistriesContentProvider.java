/*
 * @(#)RegistriesContentProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemregistry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.server.manager.registry.MeemRegistryGatewayWedge;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.meem.Meem;




/**
  * <p>
  * ...
  * </p>
  * @author  mg
  * @version 1.0
  */
public class RegistriesContentProvider implements ITreeContentProvider {

	private Set registries = new HashSet();
	private Viewer viewer;

	public RegistriesContentProvider() {
		findRegistries();
	}

	private void findRegistries() {
		Meem[] registries = MeemRegistryGatewayWedge.getMeemRegistries();
		for (int i = 0; i < registries.length; i++)
			addRegistry(registries[i]);
		
		//addRegistry(MeemRegistryHelper.locateMeem(MeemUtility.createMeemPath(Space.TRANSIENT, MeemRegistry.IDENTIFIER)));
	}

	private void addRegistry(Meem meem) {
		// make a new client
		MeemRegistryClientImpl client = new MeemRegistryClientImpl(this, meem);
		registries.add(client);		
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MeemRegistryClientImpl)
			return ((MeemRegistryClientImpl) parentElement).getChildren();

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
		if (element instanceof MeemRegistryClientImpl) {
			return ((MeemRegistryClientImpl) element).hasChildren();
		}
		return false;
	}

	/**
	 * This returns all the root elements. 
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return registries.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		if (!registries.isEmpty()) {

			for (Iterator i = registries.iterator(); i.hasNext();) {
				MeemRegistryClientImpl client = (MeemRegistryClientImpl) i.next();
				client.dispose();
			}

			registries.clear();
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
			SWTClientSynchronizer.get(display).execute(new Runnable() {
				public void run() {
					viewer.refresh();
				}
			});
		}
	}

}
