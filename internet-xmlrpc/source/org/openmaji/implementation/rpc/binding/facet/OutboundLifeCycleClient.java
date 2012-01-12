/*
 * Copyright 2005 by Majitek.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.HashMap;

import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.rpc.binding.OutboundBinding;

/**
 * outbound binding for the LifeCycleClient, that connects to inbound 
 * LifeCycleClient facet on the meem.
 * 
 * @author Ravishankar Hiremath
 */
public class OutboundLifeCycleClient
    extends OutboundBinding
    implements LifeCycleClient
{
    /**
     * constructor
     *
     */
    public OutboundLifeCycleClient()
    {
        setFacetClass(LifeCycleClient.class);
    }

    /* (non-Javadoc)
     * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClient#lifeCycleStateChanging(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
     */
    public void lifeCycleStateChanging(LifeCycleTransition transition)
    {
        send("lifeCycleStateChanging", new Serializable[]{ getTransitionTable(transition) });
        
    }

    /* (non-Javadoc)
     * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClient#lifeCycleStateChanged(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
     */
    public void lifeCycleStateChanged(LifeCycleTransition transition)
    {
        send("lifeCycleStateChanged", new Serializable[]{ getTransitionTable(transition) });

    }

    private HashMap<String, String> getTransitionTable(LifeCycleTransition transition)
    {
    	HashMap<String, String> table = new HashMap<String, String>();
        table.put("previousLifeCycleState", transition.getPreviousState().getCurrentState());
        table.put("currentLifeCycleState", transition.getCurrentState().getCurrentState());
        
        return table;
    }
}
