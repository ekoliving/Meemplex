/*
 * @(#)SubsystemFactoryNode.java
 * Created on 5/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes;


import org.eclipse.jface.viewers.StructuredViewer;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleManagerProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.SubsystemFactoryProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.UnavailableMeemNode;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient;
import org.openmaji.system.space.CategoryEntry;


/**
 * <code>SubsystemFactoryNode</code>.
 * <p>
 * @author Kin Wong
 */
public class SubsystemFactoryNode extends MeemSetNode {
	private static final long serialVersionUID = 6424227717462161145L;

	LifeCycleManagerProxy lifeCycleManagerProxy = null;
	
  private SubsystemFactoryClient deploymentClient = new SubsystemFactoryClient() {
  	/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#definitionsAdded(org.openmaji.meem.definition.MeemDefinition[])
		 */
		public void definitionsAdded(MeemDefinition[] meemDefinitions) {
			// -mg- Auto-generated method stub
		}
		
		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#definitionsRemoved(org.openmaji.meem.definition.MeemDefinition[])
		 */
		public void definitionsRemoved(MeemDefinition[] meemDefinitions) {
			// -mg- Auto-generated method stub
		}
		
		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#subsystemCreated(org.openmaji.meem.Meem, org.openmaji.meem.definition.MeemDefinition)
		 */
		public void subsystemCreated(Meem meem, MeemDefinition meemDefinition) {
			if(!hasRefreshed()) return;	// The node has not yet been expanded
			addChild(meem.getMeemPath().toString(), new UnavailableMeemNode(new CategoryEntry(meem.getMeemPath().toString(), meem), SubsystemFactoryNode.this));
		}
		
		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#subsystemDestroyed(org.openmaji.meem.Meem)
		 */
		public void subsystemDestroyed(Meem meem) {
			if(!hasRefreshed()) return;	// The node has not yet been expanded
			removeChild(meem.getMeemPath().toString());
		}
	};

	/**
	 * Constructs an instance of <code>SubsystemFactoryNode</code>.
	 * <p>
	 * @param label
	 * @param proxy
	 */
	public SubsystemFactoryNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}

	public SubsystemFactoryProxy getSubsystemFactory() {
		return getProxy().getSubsystemFactoryProxy();
	}
	
	public LifeCycleManagerProxy getLifeCycleManagerProxy() {
		if (lifeCycleManagerProxy == null) {
			lifeCycleManagerProxy = (LifeCycleManagerProxy) getProxy().getFacetProxy(LifeCycleManagerProxy.class);
		}

		return lifeCycleManagerProxy;
	}
	
	public void activate(StructuredViewer viewer) {
		super.activate(viewer);
		getSubsystemFactory().addClient(deploymentClient);
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node#deactivate()
	 */
	public void deactivate() {
		getSubsystemFactory().removeClient(deploymentClient);
		super.deactivate();
	}
	
	/**
	 */
	public Node createNodeFromProxy(MeemClientProxy proxy) {
		return new SubsystemNode(proxy);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.MeemSetNode#getMeemsFromProxy()
	 */
	protected Meem[] getMeemsFromProxy() {
		return getSubsystemFactory().getSubsystems();
	}
}
