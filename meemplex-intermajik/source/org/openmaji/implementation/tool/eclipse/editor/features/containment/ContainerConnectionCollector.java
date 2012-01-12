/*
 * @(#)ContainerConnectionCollector.java
 * Created on 6/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.containment;

import java.util.List;

import org.openmaji.implementation.tool.eclipse.editor.features.connectable.ConnectionCollector;


/**
 * <code>ContainerConnectionCollector</code>.
 * <p>
 * @author Kin Wong
 */
public class ContainerConnectionCollector extends ConnectionCollector {

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.ConnectionCollector#resolveChildren(java.lang.Object)
	 */
	protected List resolveChildren(Object model) {
		if(!(model instanceof IModelContainer)) return null;
		IModelContainer container = (IModelContainer)model;
		return container.getChildren();
	}
}
