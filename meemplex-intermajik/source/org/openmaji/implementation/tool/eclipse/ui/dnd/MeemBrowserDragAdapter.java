/*
 * @(#)MeemBrowserDragAdapter.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dnd;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;

/**
	* <p>
	* ...
	* </p>
	* @author  mg
	* @version 1.0
	*/
public class MeemBrowserDragAdapter extends DragSourceAdapter {
	private TreeViewer viewer;

	public MeemBrowserDragAdapter(TreeViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		if (MeemPathTransfer.getInstance().isSupportedType(event.dataType)) {

			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			Vector meemPaths = new Vector();

			for (Iterator i = selection.iterator(); i.hasNext();) {
				Object item = i.next();

				if (item instanceof Meem) {
					Meem meem = (Meem) item;

					meemPaths.add(meem.getMeemPath());
				} else {
					// if there is a getMeemPath() method, try calling that

					try {
						Method method = item.getClass().getMethod("getMeemPath", new Class[0]);

						if (method.getReturnType().isAssignableFrom(MeemPath.class)) {
							MeemPath meemPath = (MeemPath) method.invoke(item, new Object[0]);
							meemPaths.add(meemPath);
						}
					} catch (Exception e) {
					}
				}
			}

			event.data = (MeemPath[]) meemPaths.toArray(new MeemPath[0]);
		}
	}

}
