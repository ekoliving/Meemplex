/*
 * @(#)LifeCycleLimitProxy.java
 * Created on 30/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.meem.Facet;
import org.openmaji.meem.wedge.lifecycle.LifeCycleLimit;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>LifeCycleLimitProxy</code> represents the client-side proxy of the 
 * <code>LifeCycleLimit</code> facet.
 * <p>
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleLimit
 * @author Kin Wong
 */
public class LifeCycleLimitProxy extends FacetProxy implements LifeCycleLimit {
	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(LifeCycleLimit.class, "lifeCycleLimit"),
			new FacetOutboundSpecification(LifeCycleLimit.class, "lifeCycleLimitClient"));

	static public String ID_LIFE_CYCLE_STATE_LIMIT = LifeCycleLimitProxy.class + ".LifeCycleStateLimit";
	
	private LifeCycleState limit;	
	
	//=== Internal LifeCycleLimit Client Implementation ==========================
	LifeCycleLimit lifeCycleLimitClient = new LocalLifeCycleLimit(this);
	
	public static class LocalLifeCycleLimit
		implements LifeCycleLimit {
		LifeCycleLimitProxy parent;
		
		LocalLifeCycleLimit(LifeCycleLimitProxy parent)
		{
			this.parent = parent;
		}
		
		public synchronized void limitLifeCycleState(final LifeCycleState state) {
			parent.limit = state;
			if(parent.containsClient()) {
				parent.getSynchronizer().execute(new Runnable() {
					public void run() { parent.fireLimitLifeCycleState(state);}
				});
			}
		}
	};
	
	/**
	 * Constructs an instance of <code>LifeCycleLimitProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 * @param specs
	 */
	public LifeCycleLimitProxy(
		MeemClientProxy meemClientProxy,
		FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
		clearContent();
	}
	
	/**
	 * Constructs an instance of <code>LifeCycleLimitProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 */
	public LifeCycleLimitProxy(
		MeemClientProxy meemClientProxy) {
		super(meemClientProxy, defaultSpecs);
		clearContent();
	}
	
	/**
	 * Gets the cached Life Cycle state limit.
	 * <p>
	 * @return The cached Life Cycle state limit.
	 */
	public LifeCycleState getLifeCycleStateLimit() {
		return limit;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected Facet getOutboundTarget() {
		return lifeCycleLimitClient;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	protected void clearContent() {
		limit = LifeCycleState.ABSENT;	
	}

	/**
	 * Gets the inbound reference as LifeCycleLimit.
	 * <p>
	 * @return The inbound reference as LifeCycleLimit.
	 */
	private LifeCycleLimit getLifeCycleLimit() {
		return (LifeCycleLimit)getInboundReference();
	}
	
	//=== Client Management ======================================================	
	private void fireLimitLifeCycleState(LifeCycleState state) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			LifeCycleLimit client = (LifeCycleLimit)clients[i];
			client.limitLifeCycleState(state);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */	
	protected void realizeClientContent(Object client) {
		LifeCycleLimit lifeCycleLimit = (LifeCycleLimit)client;
		lifeCycleLimit.limitLifeCycleState(getLifeCycleStateLimit());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */	
	protected void clearClientContent(Object client) {
		LifeCycleLimit lifeCycleLimit = (LifeCycleLimit)client;
		lifeCycleLimit.limitLifeCycleState(LifeCycleState.ABSENT);
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#isEssential()
	 */
	protected boolean isEssential() {
		return true;
	}
	
	//=== External LifeCycleLimit ================================================
	/* (non-Javadoc)
	 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleLimit#limitLifeCycleState(org.openmaji.meem.wedge.lifecycle.LifeCycleState)
	 */
	public void limitLifeCycleState(final LifeCycleState state) {
		if(isReadOnly()) return;

		getLifeCycleLimit().limitLifeCycleState(state);
	}
}
