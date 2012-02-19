/*
 * @(#)MeemSetNode.java
 * Created on 12/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.UnavailableMeemNode;
import org.openmaji.meem.Meem;
import org.openmaji.system.space.CategoryEntry;



/**
 * <code>MeemSetNode</code> represents a node that dynamically
 * fills its children with a map that maps Maji meems to Nodes.
 * <p>
 * @author Kin Wong
 */
abstract public class MeemSetNode extends MeemNode {
	/**
	 * Constructs an instance of <code>MeemSetNode</code>.
	 * <p>
	 * @param label
	 * @param proxy
	 */
	protected MeemSetNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}

	protected MeemSetNode(MeemClientProxy proxy) {
		super(null, proxy);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node#initialExpandRequired()
	 */
	protected boolean initialExpandRequired() {
		return true;
	}
	
	public void refreshChildren() {
		Meem[] meems = getMeemsFromProxy();
		for(int i = 0; i < meems.length; i++) {
			Meem meem = meems[i];
			Node node = new UnavailableMeemNode(new CategoryEntry(meem.getMeemPath().toString(), meem), this);// createNodeFromMeem(meem);
			addChildInternal(meem.getMeemPath().toString(), node);
		}
	}
	
	abstract protected Meem[] getMeemsFromProxy();
	abstract public Node createNodeFromProxy(MeemClientProxy proxy);
}
