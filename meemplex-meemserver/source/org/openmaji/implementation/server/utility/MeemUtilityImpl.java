/*
 * @(#)MeemUtilityImpl.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.utility;

import org.openmaji.implementation.server.gateway.ServerGatewayImpl;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.server.helper.MeemHelper;
import org.openmaji.server.helper.MetaMeemHelper;
import org.openmaji.server.helper.ReferenceHelper;



/**
 * SPI class for MeemHelper.
 */
public class MeemUtilityImpl
    implements org.openmaji.system.utility.MeemUtility
{
    //private ServerGatewayImpl gateway;

    public MeemUtilityImpl()
    {
       //this.gateway = null;
    }
    public MeemUtilityImpl(
        ServerGatewayImpl	gateway)
    {
        //this.gateway = gateway;
    }
    
    /* (non-Javadoc)
     * @see org.openmaji.system.utility.MeemUtility#isA(org.openmaji.meem.Meem, java.lang.Class)
     */
    public boolean isA(final Meem meem, final Class specification)
    {
        return Boolean.valueOf(MeemHelper.isA(meem, specification)).booleanValue();
    }

    /* (non-Javadoc)
     * @see org.openmaji.system.utility.MeemUtility#hasA(org.openmaji.meem.Meem, java.lang.String, java.lang.Class, org.openmaji.meem.definition.Direction)
     */
    public boolean hasA(final Meem meem, final String facetIdentifier, final Class specification, final Direction direction)
    {
        return Boolean.valueOf(MeemHelper.hasA(meem, facetIdentifier, specification, direction)).booleanValue();
    }
    
    /* (non-Javadoc)
     * @see org.openmaji.system.utility.MeemUtility#getTarget(org.openmaji.meem.Meem, java.lang.String, java.lang.Class)
     */
    public Facet getTarget(final Meem meem, final String facetIdentifier, final Class specification)
    {
        return ReferenceHelper.getTarget(meem, facetIdentifier, specification);
    }
    
    /*
	 * @see org.openmaji.system.utility.MeemUtility#getMeemDefinition(org.openmaji.meem.Meem)
	 */
	public MeemDefinition getMeemDefinition(final Meem meem) 
	{
	    return MetaMeemHelper.getMeemDefinition(meem);
	}
}
