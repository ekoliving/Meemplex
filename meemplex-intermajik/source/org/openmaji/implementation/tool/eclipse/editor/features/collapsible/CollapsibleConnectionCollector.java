/*
 * @(#)CollapsibleConnectionCollector.java
 * Created on 16/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

import java.util.List;

import org.openmaji.implementation.tool.eclipse.editor.features.connectable.*;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer;


/**
 * <code>CollapsibleConnectionCollector</code>.
 * <p>
 * @author Kin Wong
 */
public class CollapsibleConnectionCollector
	extends ConnectionCollector {
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.collapsible.ConnectionCollector#resolveChildren(java.lang.Object)
	 */
	protected List resolveChildren(Object model) {
		if(!(model instanceof IModelContainer)) return null;
		IModelContainer container = (IModelContainer)model;
		
		if(model instanceof ICollapsible) {
				ICollapsible collapsible = (ICollapsible)model;
				if(!collapsible.isCollapsed()) return null;
		}
		return container.getChildren();
	}

}
