/*
 * @(#)MeemFactoryImpl.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem;

import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.server.helper.LifeCycleManagerHelper;
import org.openmaji.system.meem.MeemFactory;



/**
 * Spi factory for  creating meems.
 */
public class MeemFactoryImpl implements MeemFactory
{	
	/* (non-Javadoc)
	 * @see org.openmaji.meem.MeemFactory#create(org.openmaji.meem.definition.MeemDefinition, org.openmaji.meem.Meem)
	 */
	public Meem create(final MeemDefinition meemDefinition, final Meem lifeCycleManagerMeem)
	{
        return LifeCycleManagerHelper.createMeem(meemDefinition, lifeCycleManagerMeem);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.meem.MeemFactory#create(org.openmaji.meem.definition.MeemDefinition, org.openmaji.meem.Meem, org.openmaji.meem.wedge.lifecycle.LifeCycleState)
	 */
	public Meem create(final MeemDefinition meemDefinition, final Meem lifeCycleManagerMeem, final LifeCycleState initialState)
	{
          return LifeCycleManagerHelper.createMeem(meemDefinition, lifeCycleManagerMeem, initialState);
	}
}
