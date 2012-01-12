package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;

import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.rpc.binding.OutboundBinding;

/**
 * outbound binding for the LifeCycle
 * 
 */
public class OutboundLifeCycle
    extends OutboundBinding
    implements LifeCycle
{
    /**
     * constructor
     */
    public OutboundLifeCycle()
    {
        setFacetClass(LifeCycle.class);
    }

    public void changeLifeCycleState(LifeCycleState state) {
        send("changeLifeCycleState", new Serializable[]{state.getCurrentState()} );
    }
}
