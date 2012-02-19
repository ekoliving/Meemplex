/*
 * @(#)WorksheetManagerNode.java
 * Created on 13/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes;

import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategoryClient;
import org.openmaji.system.space.CategoryClient;



/**
 * <code>WorksheetManagerNode</code>.
 * <p>
 * @author Kin Wong
 */
public class WorksheetManagerNode extends CategoryNode {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * Constructs an instance of <code>WorksheetManagerNode</code>.
	 * <p>
	 * @param label
	 * @param proxy
	 */
	public WorksheetManagerNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}
	
	protected class LCMNodeCategoryClient 
		extends NodeCategoryClient 
		implements LifeCycleManagerCategoryClient {
		}
		
		/* (non-Javadoc)
		 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode#createCategoryClient()
		 */
		protected CategoryClient createCategoryClient() {
			return new LCMNodeCategoryClient();
		}
	
	/**
	 * Creates the child node based on the category entry.
	 * @param entry The category entry of the node.
	 * @return A node representing the meem.
	 */
		protected Node createNode(String name, MeemClientProxy proxy) {
			return new MeemNode(proxy);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode#getCategory()
	 */
	public CategoryProxy getCategory() {
		return getProxy().getLCMCategoryProxy();
	}
}
