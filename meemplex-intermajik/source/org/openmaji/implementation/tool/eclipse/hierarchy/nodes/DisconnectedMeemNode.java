/*
 * @(#)DisconnectedMeemNode.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;

/**
 * @author mg
 */
public class DisconnectedMeemNode extends LabelNode {
	private static final long serialVersionUID = 6424227717462161145L;

	final MeemNode meemNode;
	String name;
	boolean gotLoaded = false;
	
	public DisconnectedMeemNode(String name, MeemNode meemNode) {
		this.meemNode = meemNode;
		this.name = name;
		
		meemNode.getProxy().getLifeCycle().addClient(lifeCycleClient);
	}
	
	private synchronized void disposeLifeCycleClient() {
		if (lifeCycleClient != null) {
			meemNode.getProxy().getLifeCycle().removeClient(lifeCycleClient);
			lifeCycleClient = null;
		}
	}

	LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		
		public synchronized void lifeCycleStateChanged(LifeCycleTransition transition) {
			checkState(transition.getCurrentState());
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			//don't care
		}

	};
	
	private boolean checkState(LifeCycleState currentState) {
		int index = LifeCycleState.STATES.indexOf(currentState);
		int indexLoaded = LifeCycleState.STATES.indexOf(LifeCycleState.LOADED);
		if (index >= indexLoaded) {
			if (!gotLoaded) {
				gotLoaded = true;
				meemNode.getProxy().getSynchronizer().execute(new Runnable() {  
					public void run() {
						swapProxy();
					}
				});				
				
				return true;
			}
		}
		return false;
	}
	
	private void swapProxy() {
		Node parent = getParent();
		if (parent != null && parent.removeChildInternal(name)) {
			parent.addChild(name, meemNode);
		}
		disposeLifeCycleClient();
	}
	
}
