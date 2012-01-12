/*
 * @(#)MeemServerManagerNode.java
 * Created on 26/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;

/**
 * <code>MeemServerManagerNode</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemServerManagerNode extends CategoryNode {
	private static final long serialVersionUID = 6424227717462161145L;

	public MeemServerManagerNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.CategoryNode#createNode(org.openmaji.system.space.CategoryEntry)
	 */
	protected Node createNode(String name, MeemClientProxy proxy) {
		return new MeemServerNode(name, proxy);
	}

}
