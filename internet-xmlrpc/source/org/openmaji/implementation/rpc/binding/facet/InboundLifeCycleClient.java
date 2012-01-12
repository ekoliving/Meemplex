/*
 * Copyright 2005 by Majitek.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.util.Hashtable;

import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.rpc.binding.InboundBinding;

/**
 * inbound binding for the LifeCycleClient, that connects to outbound 
 * LifeCycleClient facet on the meem.
 *  
 * @author Ravishankar Hiremath
 */
public class InboundLifeCycleClient
    extends InboundBinding
{
	public InboundLifeCycleClient() 
	{
		setFacetClass(LifeCycleClient.class);
	}

	/**
	 * Add a lifecylceclient facet to send values to.
	 * 
	 * @param listener
	 */
	public void addLifeCycleClientFacet(LifeCycleClient listener) 
	{
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeLifeCycleClientFacet(LifeCycleClient listener)
	{
		removeListener(listener);
	}
	
	protected void invoke(String method, Object[] params) 
	{
		if ("lifeCycleStateChanging".equals(method) ) 
		{
			((LifeCycleClient)proxy).lifeCycleStateChanging( getLifeCycleTransition(params) );
		}
		else if("lifeCycleStateChanged".equals(method))
		{
		    ((LifeCycleClient)proxy).lifeCycleStateChanged( getLifeCycleTransition(params) );
		}
	}

	private LifeCycleTransition getLifeCycleTransition(Object[] params)
	{
	    Hashtable table = (Hashtable) params[0];
	    LifeCycleState previousLifeCycleState = 
	        new LifeCycleState((String)table.get("previousLifeCycleState"));
	    LifeCycleState currentLifeCycleState = 
	        new LifeCycleState((String)table.get("currentLifeCycleState"));
	    
	    return new LifeCycleTransition(previousLifeCycleState, currentLifeCycleState);
	    
	}


}
