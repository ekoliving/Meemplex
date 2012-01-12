/*
 * @(#)LifeCycleProxy.java
 * Created on 2/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;


import org.openmaji.meem.Facet;
import org.openmaji.meem.wedge.lifecycle.*;

/**
 * <code>LifeCycleProxy</code> represents the client-side proxy of LifeCycle 
 * facet.<p>
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycle
 * @author Kin Wong
 */
public class LifeCycleProxy extends FacetProxy implements LifeCycle {
	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(LifeCycle.class, "lifeCycle"),
			new FacetOutboundSpecification(LifeCycleClient.class, "lifeCycleClient"));

	static public String ID_LIFE_CYCLE_STATE = LifeCycleProxy.class + ".LifeCycleState";

	private LifeCycleTransition transition;
	
	//=== Internal LifeCycle Client Implementation ===============================
	public static class LocalLifeCycleClient
		implements LifeCycleClient
	{
		LifeCycleProxy	p;
		
		public LocalLifeCycleClient(
			LifeCycleProxy	p)
		{
			this.p = p;
		}
		
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			//don't care
		}
		
		public void lifeCycleStateChanged(final LifeCycleTransition transition) {
			if(p.transition.equals(transition)) return;
			p.transition = transition;

			if(p.containsClient())
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireLifeStateChanged(transition);
				}
			});
		}
	}
	
	private LifeCycleClient lifeCycleClient = new LocalLifeCycleClient(this);

	/**
	 * Constructs an instance of <code>LifeCycleProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 */
	public LifeCycleProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}
	
	public LifeCycleProxy(MeemClientProxy meemClientProxy) {
		this(meemClientProxy, defaultSpecs);
	}
	
	private LifeCycle getLifeCycle() {
		return (LifeCycle)getInboundReference();
	}

	/**
	 * Gets the last life cycle transition of the underlying meem.<p>
	 * @return LifeCycleTransition the last life cycle transition of the 
	 * underlying meem.
	 */
	public LifeCycleTransition getTransition() {
		return transition; 
	}
	
	/**
	 * Gets the last <code>LifeCycleState</code> of the Meem this proxy 
	 * represents.
	 * @return LifeCycleState The last <code>LifeCycleState</code> of the meem 
	 * this proxy represents.
	 */
	public LifeCycleState getState() {
		return transition.getCurrentState();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#isEssential()
	 */
	protected boolean isEssential() {
		return true;
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected Facet getOutboundTarget() {
		return lifeCycleClient;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	protected void clearContent() {
		transition = 
			new LifeCycleTransition(LifeCycleState.ABSENT, LifeCycleState.ABSENT);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#disconnect()
	 */
	protected void disconnect() {
		if(!isConnected()) return;
		disconnectInbound();
		connected = false;
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#connect()
	 */
	protected void connect() {
		if(isConnected()) return;
		if (outboundReference == null) {
			connectOutbound();
		}
		// always disconnect to make sure we always have an up to date proxy
		disconnectInbound();
		if (!(getState().equals(LifeCycleState.ABSENT) || getState().equals(LifeCycleState.DORMANT))) {
			connected = connectInbound();
		}
		
	}
	
	//=== Client Management ======================================================	
	private void fireLifeStateChanged(LifeCycleTransition transition) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			LifeCycleClient client = (LifeCycleClient)clients[i];
			client.lifeCycleStateChanged(transition);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */	
	protected void realizeClientContent(Object client) {
		LifeCycleClient lifeCycleClient = (LifeCycleClient)client;
		lifeCycleClient.lifeCycleStateChanged(getTransition());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */	
	protected void clearClientContent(Object client) {
		LifeCycleClient lifeCycleClient = (LifeCycleClient)client;

		LifeCycleTransition endTransition = 
			new LifeCycleTransition(getState(), LifeCycleState.ABSENT);
		lifeCycleClient.lifeCycleStateChanged(endTransition);
	}

	//=== External LifeCycle Implementation ======================================
	/* (non-Javadoc)
	 * @see org.openmaji.meem.aspect.wedge.lifecycle.LifeCycle#lifeCycleStateChanged(org.openmaji.meem.aspect.wedge.lifecycle.LifeCycleState)
	 */
	public void changeLifeCycleState(final LifeCycleState state) {
		if(isReadOnly()) return;
		
		getLifeCycle().changeLifeCycleState(state);
	}
}
