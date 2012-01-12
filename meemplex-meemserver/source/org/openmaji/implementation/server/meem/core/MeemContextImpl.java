/*
 * @(#)MeemContextImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.core;

import org.openmaji.implementation.server.meem.WedgeImpl;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.system.meem.core.MeemCore;



/**
 * Base implementation for the meem context applicable to a wedge.
 */
public class MeemContextImpl 
	implements MeemContext
{
	MeemCore	meemCore;
	WedgeImpl	wedge;
	
    public MeemContextImpl(
    	MeemCore	meemCore,
    	WedgeImpl	wedge)
    {
    	this.meemCore = meemCore;
    	this.wedge = wedge;
    }
    
    /**
     * 
     */
    public Meem getSelf()
    {
        return meemCore.getSelf();
    }

    /**
     * 
     */
    public Meem getThreadManager()
    {
        return meemCore.getThreadManager();
    }

    /**
     * 
     */
    public Meem getMeemStore()
    {
        return meemCore.getMeemStore();
    }

    /**
     * 
     */
    public Meem getLifeCycleManager()
    {
        return meemCore.getLifeCycleManager();
    }

    /**
     * 
     */
    public Meem getMeemRegistry()
    {
        return meemCore.getMeemRegistry();
    }

    /**
     * 
     */
    public Meem getFlightRecorder()
    {
		return meemCore.getFlightRecorder();
    }

    /**
     * Provide the value of an ImmutableAttribute.
     *
     * @param key Index for the required ImmutableAttribute value
     * @return ImmutableAttribute value for the given key
     * @exception IllegalArgumentException ImmutableAttribute key isn't valid
     */

    public Object getImmutableAttribute(
      Object key)
      throws IllegalArgumentException {

      return(meemCore.getImmutableAttribute(key));
    }
    
    /**
     * 
     */
    public String getWedgeIdentifier()
    {
		return wedge.getWedgeAttribute().getIdentifier();
    }

    /**
     * 
     */
    public Facet getTarget(String facetIdentifier)
    {
		return meemCore.getTarget(facetIdentifier);
    }

    /**
     * 
     */
    public <T extends Facet> T getTargetFor(T facet, Class<T> specification)
    {
		return meemCore.getTargetFor(facet, specification);
    }

    /**
     * 
     */
    public <T extends Facet> T getLimitedTargetFor(T facet, Class<T> specification)
    {
		return meemCore.getLimitedTargetFor(facet, specification);
    }

    /**
     * 
     */
    public <T extends Facet> T  getNonBlockingTargetFor(T facet, Class<T> specification)
    {
        return meemCore.getNonBlockingTargetFor(facet, specification);
    }
}
