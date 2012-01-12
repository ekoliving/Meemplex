/*
 * @(#)NodeLabelProvider.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.DisconnectedMeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.UnavailableMeemNode;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>NodeLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class NodeLabelProvider extends LabelProvider {
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if(!(element instanceof Node)) return super.getText(element);
		Node node = (Node)element;
		return node.getText();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof UnavailableMeemNode) {
			return Images.ICON_MEEM_UNAVAILABLE.createImage();
		}
		if (element instanceof DisconnectedMeemNode) {
			return Images.ICON_MEEM_UNAVAILABLE.createImage();
		}
		return null;
	}
}
