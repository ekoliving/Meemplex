/*
 * @(#)LifeCycleManagerDefinitionFactoryImpl.java
 *
 *  Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 *  This software is the proprietary information of EkoLiving Pty Ltd.
 *  Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle;

import org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerMeem;
import org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerDefinitionFactory;
import org.openmaji.system.meem.definition.DefinitionFactory;




/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class LifeCycleManagerDefinitionFactoryImpl implements LifeCycleManagerDefinitionFactory {

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerDefinitionFactory#createPersisting(java.lang.Class[])
	 */
	public MeemDefinition createPersisting(Class[] wedges) {
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(PersistingLifeCycleManagerMeem.class);

		return addApplicationWedges(meemDefinition, wedges);
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerDefinitionFactory#createTransient(java.lang.Class[])
	 */
	public MeemDefinition createTransient(Class[] wedges) {
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(TransientLifeCycleManagerMeem.class);
		
		return addApplicationWedges(meemDefinition, wedges);
	}
	
	private MeemDefinition addApplicationWedges(MeemDefinition meemDefinition, Class[] applicationWedges) {
		for (int i = 0; i < applicationWedges.length; i++) {
			WedgeDefinition wedgeDefinition = DefinitionFactory.spi.create().createWedgeDefinition(applicationWedges[i]);
			
			meemDefinition.addWedgeDefinition(wedgeDefinition);
		}
		
		return meemDefinition;
		
	}

}
