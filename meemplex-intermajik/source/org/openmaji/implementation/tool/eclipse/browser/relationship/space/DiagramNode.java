/*
 * @(#)DiagramNode.java
 * Created on 28/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;

/**
 * <code>DiagramNode</code>.
 * <p>
 * @author Kin Wong
 */
public class DiagramNode extends CategoryNode {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * Constructs an instance of <code>DiagramNode</code>.
	 * <p>
	 * @param label
	 * @param proxy
	 */
	public DiagramNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}
}
