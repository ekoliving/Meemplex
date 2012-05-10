/*
 * @(#)MeemNode.java
 * Created on 28/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;


import java.io.Serializable;

import org.eclipse.jface.viewers.StructuredViewer;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * <code>MeemNode</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemNode extends LabelNode {
	private static final long serialVersionUID = 6424227717462161145L;

	transient private MeemClientProxy proxy;

	private synchronized void disposeLifeCycleClient() {
		if (lifeCycleClient != null) {
			getProxy().getLifeCycle().removeClient(lifeCycleClient);
			lifeCycleClient = null;
		}
	}

	transient LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		public synchronized void lifeCycleStateChanged(LifeCycleTransition transition) {

			if (!disposed)
			{
				LifeCycleState state = transition.getCurrentState();
				if(transition.equals(LifeCycleTransition.LOADED_DORMANT)) {

					disposed = true;
					disposeLifeCycleClient();

					if(getParent() != null) swapProxy();//getParent().refreshChildren();
				}
				else if(state.equals(LifeCycleState.ABSENT)) {
					refreshVisual();
				}
				else if (transition.equals(LifeCycleTransition.DORMANT_LOADED)) {
					if(getParent() != null) 
						getParent().refreshChildren();
				}
				else {
					updateVisual();
				}
			}
		}
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			//don't care
		}
		private boolean disposed = false;
	};
	
	transient ConfigurationClient configurationClient = new ConfigurationClient() {
		public void specificationChanged(ConfigurationSpecification[] arg0, ConfigurationSpecification[] arg1) {
			updateVisual();
		}

		public void valueAccepted(ConfigurationIdentifier arg0, Serializable arg1) {
			updateVisual();
		}
		public void valueRejected(ConfigurationIdentifier arg0, Serializable arg1, Serializable arg2) {}
	};
	
	private void swapProxy() {
		deactivate();
		Node parent = getParent();
		String newKey = getText() == "" ? proxy.getMeemPath().toString() : getText();
		Node newNode = new DisconnectedMeemNode(newKey, this);
		parent.removeChildInternal(newKey);
		parent.addChild(newKey, newNode);
		parent.refreshVisual();
	}
	
	protected Node createNode(String name, MeemClientProxy proxy) {
		return null;
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(adapter == MeemClientProxy.class) {
			return proxy;
		}
		else
		return super.getAdapter(adapter);
	}


	public MeemClientProxy getProxy() {
		return proxy;
	}

	public MeemNode(String label, MeemClientProxy proxy) {
		super(label);
		this.proxy = proxy;
	}
	
	public MeemNode(MeemClientProxy proxy) {
		this(null, proxy);
	}

	public MeemPath getMeemPath() {
		return proxy.getMeemPath();
	}

	protected void activate(StructuredViewer viewer) {
		super.activate(viewer);
		getProxy().getLifeCycle().addClient(lifeCycleClient);
		getProxy().getConfigurationHandler().addClient(configurationClient);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node#deactivate()
	 */	
	protected void deactivate() {
		getProxy().getConfigurationHandler().removeClient(configurationClient);
		disposeLifeCycleClient();
		super.deactivate();
	}

}
