/*
 * @(#)SubsystemNode.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.openmaji.implementation.tool.eclipse.client.*;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.UnavailableMeemNode;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategoryClient;
import org.openmaji.system.manager.lifecycle.subsystem.CommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemState;
import org.openmaji.system.space.CategoryEntry;


/**
 * <code>SubsystemNode</code>.
 * <p>
 * @author Peter
 */
public class SubsystemNode extends MeemSetNode {
	private static final long serialVersionUID = 6424227717462161145L;

	LifeCycleManagerProxy lifeCycleManagerProxy = null;
	
	transient private LifeCycleManagerCategoryClient lcmCategoryClient = new LifeCycleManagerCategoryClient() {
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesAdded(CategoryEntry[] newEntries) {
			if(!hasRefreshed()) return;	// The node has not yet been expanded
			
			for (int i = 0; i < newEntries.length; i++) {
				addChild(newEntries[i].getMeem().getMeemPath().toString(), new UnavailableMeemNode(new CategoryEntry(newEntries[i].getMeem().getMeemPath().toString(), newEntries[i].getMeem()), SubsystemNode.this));
			}
		}
		
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesRemoved(CategoryEntry[] removedEntries) {
			if(!hasRefreshed()) return;	// The node has not yet been expanded
			
			for (int i = 0; i < removedEntries.length; i++) {
				removeChild(removedEntries[i].getMeem().getMeemPath().toString());
			}
		}
		
		/**
		 * @see org.openmaji.system.space.CategoryClient#entryRenamed(org.openmaji.system.space.CategoryEntry, org.openmaji.system.space.CategoryEntry)
		 */
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			if(!hasRefreshed()) return;	// The node has not yet been expanded
			refreshVisual();
		}
		
		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
		 */
		public void contentFailed(String reason) {
			// don't care
		}
		
		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent() {
			// don't care
		}
	};
	
	transient private SubsystemClient subsystemClient = new SubsystemClient() {

  	/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient#commissionStateChanged(org.openmaji.system.manager.lifecycle.subsystem.CommissionState)
		 */
		public void commissionStateChanged(CommissionState commissionState) {
			refreshVisual();
		}
		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient#meemCreated(org.openmaji.meem.Meem, org.openmaji.meem.definition.MeemDefinition)
		 */
		public void meemCreated(Meem meem, MeemDefinition meemDefinition) {
			// don't care

		}
		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient#meemsAvailable(org.openmaji.meem.definition.MeemDefinition[], org.openmaji.system.manager.lifecycle.subsystem.MeemDescription[])
		 */
		public void meemsAvailable(MeemDefinition[] meemDefinitions, MeemDescription[] meemDescriptions) {
			// don't care

		}
		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient#subsystemStateChanged(org.openmaji.system.manager.lifecycle.subsystem.SubsystemState)
		 */
		public void subsystemStateChanged(SubsystemState subsystemState) {
			refreshVisual();
		}
	};
	
	public SubsystemNode(MeemClientProxy proxy) {
		super(proxy);
		getLifeCycleManagerProxy();
	}
	
	public SubsystemProxy getSubsystem() {
		return getProxy().getSubsystem();
	}
	
	private LCMCategoryProxy getLCMCategoryProxy() {
		return getProxy().getLCMCategoryProxy();
	}
	
	public LifeCycleManagerProxy getLifeCycleManagerProxy() {
		if (lifeCycleManagerProxy == null) {
			lifeCycleManagerProxy = (LifeCycleManagerProxy) getProxy().getFacetProxy(LifeCycleManagerProxy.class);
		}

		return lifeCycleManagerProxy;
	}

	/**
	 */
	public void activate(StructuredViewer viewer) {
		super.activate(viewer);
		getSubsystem().addClient(subsystemClient);
		getLCMCategoryProxy().addClient(lcmCategoryClient);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode#deactivate()
	 */
	public void deactivate() {
		getLCMCategoryProxy().removeClient(lcmCategoryClient);
		getSubsystem().removeClient(subsystemClient);
		super.deactivate();
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.MeemSetNode#getMeemsFromProxy()
	 */
	protected Meem[] getMeemsFromProxy() {
		ArrayList<Meem> meems = new ArrayList<Meem>();
		Iterator iter = getLCMCategoryProxy().getEntries();
		while (iter.hasNext()) {
			CategoryEntry entry = (CategoryEntry) iter.next();
			meems.add(entry.getMeem());
		}
		return (Meem[])meems.toArray(new Meem[0]);
	}

	public Node createNodeFromProxy(MeemClientProxy proxy) {
		return new MeemNode(proxy);
	}
}
