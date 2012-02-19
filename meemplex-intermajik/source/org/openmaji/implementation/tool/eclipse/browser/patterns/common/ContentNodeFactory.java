/*
 * @(#)ContentNodeFactory.java
 * Created on 18/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;

import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;

/**
 * <code>ContentNodeFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class ContentNodeFactory implements IContentNodeFactory {
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.IContentNodeFactory#createContentNode(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public Node createContentNode(Node node) {
		if(node instanceof CategoryNode) {
			CategoryNode category = (CategoryNode)node;
			return new CategoryNode(category.getText(), category.getProxy());
		}
		else
		if(node instanceof MeemNode) {
			MeemNode meem = (MeemNode)node;
			return new MeemNode(meem.getText(), meem.getProxy());
		}
		else
		return null;
	}
}
