/*
 * @(#)DependencyNode.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.dependency;

import java.io.Serializable;
import java.util.Iterator;

import org.eclipse.jface.viewers.TreeViewer;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.MetaMeemProxy;
import org.openmaji.implementation.tool.eclipse.client.MetaMeemStub;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.*;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MetaMeem;



/**
 * <code>DependencyNode</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyNode extends MeemNode {
	private static final long serialVersionUID = 6424227717462161145L;

	private MetaMeem metaMeemClient = new MetaMeemStub() {
		public void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute) {
		}
		
		public void updateDependencyAttribute(DependencyAttribute dependencyAttribute) {
		}
		
		public void removeDependencyAttribute(Serializable dependencyKey) {
		}
	};
	/**
	 * Constructs an instance of <code>DependencyNode</code>.
	 * <p>
	 * @param label
	 * @param proxy
	 */
	public DependencyNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}
	
	public void activate(TreeViewer viewer) {
		super.activate(viewer);
		getMetaMeem().addClient(metaMeemClient);
	}
	
	public void deactivate() {
		getMetaMeem().removeClient(metaMeemClient);
		super.deactivate();
	}
	
	private MetaMeemProxy getMetaMeem() {
		return getProxy().getMetaMeem();
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node#refreshChildren()
	 */	
	protected void refreshChildren() {
		MeemStructure structure = getMetaMeem().getStructure();
		synchronized (structure) {
			Iterator<Serializable> it = structure.getDependencyAttributeKeys().iterator();
			while(it.hasNext()) {
				Serializable key = it.next();
				DependencyAttribute attribute = structure.getDependencyAttribute(key);
				MeemNode meemNode = createNode(attribute);
				addChildInternal(key, meemNode);
			}
		}
	}
	
	private MeemNode createNode(DependencyAttribute attribute) {
		return null;
	}
}
