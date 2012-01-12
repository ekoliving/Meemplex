/*
 * @(#)UnavailableMeemNode.java
 * Created on 24/06/2005
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.MeemSetNode;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.space.CategoryEntry;



/**
 * <code>UnavailableMeemNode</code>.
 * <p>
 * 
 * @author mg
 */
public class UnavailableMeemNode extends LabelNode {
	private static final long serialVersionUID = 6424227717462161145L;

	transient private MeemClientProxy proxy;

	String name;
	MeemNode meemNode;
	boolean gotLoaded = false;

	private synchronized void disposeLifeCycleClient() {
		if (lifeCycleClient != null) {
			getProxy().getLifeCycle().removeClient(lifeCycleClient);
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

	/**
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == MeemClientProxy.class) {
			return proxy;
		} else
			return super.getAdapter(adapter);
	}

	public MeemClientProxy getProxy() {
		return proxy;
	}

	public UnavailableMeemNode(CategoryEntry entry, MeemNode meemNode) {
		super(entry.getName());
		this.name = entry.getName();
		this.meemNode = meemNode;
//		System.err.println("UnavailableMeemNode: " + name + " : " + meemNode);
		new Thread(new AquireProxyJob(entry.getMeem().getMeemPath(), name)).start();
	}

	public MeemPath getMeemPath() {
		return proxy.getMeemPath();
	}

	private void gotProxy() {
		// check the current state
		LifeCycleProxy lifeCycleProxy = getProxy().getLifeCycle();
		LifeCycleState currentState = lifeCycleProxy.getState();
		if (!checkState(currentState)) {
			lifeCycleProxy.addClient(lifeCycleClient);
		}
		
	}
	
	private boolean checkState(LifeCycleState currentState) {
		int index = LifeCycleState.STATES.indexOf(currentState);
		int indexLoaded = LifeCycleState.STATES.indexOf(LifeCycleState.LOADED);
		if (index >= indexLoaded) {
			if (!gotLoaded) {
				gotLoaded = true;
				waitForMetaMeem();
				return true;
			}
		}
		return false;
	}
	
	private void waitForMetaMeem() {
		if (!proxy.getMetaMeem().isContentInitialized()) {
			Runnable runnable = new Runnable() {
				public void run() {
					while (!proxy.getMetaMeem().isContentInitialized()) {
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					proxy.getSynchronizer().execute(new Runnable() {  
						public void run() {
							swapProxy();
						}
					});						
					
				}
			};
			new Thread(runnable).start();
		} else {
			proxy.getSynchronizer().execute(new Runnable() {  
				public void run() {
					swapProxy();
				}
			});	
		}
	}
	
	private void swapProxy() {
		Node newNode = null;
		if (meemNode instanceof CategoryNode) {
			newNode = meemNode.createNode(name, proxy);
		} else 
		if (meemNode instanceof MeemSetNode) {
			newNode = ((MeemSetNode)meemNode).createNodeFromProxy(proxy);
		}
		
		Node parent = getParent();
		String newKey = newNode.getText() == "" ? proxy.getMeemPath().toString() : newNode.getText();
		if (parent != null && parent.removeChildInternal(name)) {
			parent.addChild(newKey, newNode);
		}
		disposeLifeCycleClient();
	}

	private class AquireProxyJob implements Runnable {

		private MeemPath meemPath;

		public AquireProxyJob(MeemPath meemPath, String name) {
			this.meemPath = meemPath;
		}

		public void run() {
//			System.err.println("Aquire Proxy for " + name + " : " + meemPath);
			proxy = InterMajikClientProxyFactory.getInstance().locate(meemPath);
//			System.err.println("Got Proxy for " + name + " : " + meemPath + " : " + proxy);
			gotProxy();
		}
	}

}