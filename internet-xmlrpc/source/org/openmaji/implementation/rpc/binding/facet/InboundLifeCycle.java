package org.openmaji.implementation.rpc.binding.facet;

import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.rpc.binding.InboundBinding;

/**
 * inbound binding for the LifeCycle
 */
public class InboundLifeCycle
    extends InboundBinding
{
	public InboundLifeCycle() 
	{
		setFacetClass(LifeCycle.class);
	}

	/**
	 * 
	 * @param listener
	 */
	public void addLifeCycleFacet(LifeCycle listener) 
	{
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeLifeCycleFacet(LifeCycle listener)
	{
		removeListener(listener);
	}

	protected void invoke(String method, Object[] params) 
	{
		if ("changeLifeCycleState".equals(method) ) 
		{
			((LifeCycle)proxy).changeLifeCycleState( getLifeCycleState(params) );
		}
	}

	private LifeCycleState getLifeCycleState(Object[] params)
	{
		return new LifeCycleState((String)params[0]);
	}


}
