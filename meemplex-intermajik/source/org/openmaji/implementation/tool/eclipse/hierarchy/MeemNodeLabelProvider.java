/*
 * @(#)SpaceBrowserLabelProvider.java
 * Created on 25/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy;

import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.client.presentation.IconExtractor;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;


/**
 * <code>SpaceBrowserLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemNodeLabelProvider extends NodeLabelProvider {
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.NodeLabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		Image image = super.getImage(element);
		if (image != null) {
			return image;
		}
		if(!(element instanceof MeemNode))  return null;
		MeemNode meem = (MeemNode)element;
		return IconExtractor.extractSmall(meem.getProxy());
	}
}
