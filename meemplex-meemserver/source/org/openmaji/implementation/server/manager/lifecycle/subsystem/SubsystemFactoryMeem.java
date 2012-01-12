/*
 * @(#)SubsystemManagerMeem.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.lifecycle.subsystem;

import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle.LifeCycleAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryWedge;
import org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerWedge;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory;



/**
 * @author Chris Kakris
 */
public class SubsystemFactoryMeem implements MeemDefinitionProvider
{
	private MeemDefinition meemDefinition = null;

	public synchronized MeemDefinition getMeemDefinition()
	{
		if (meemDefinition == null)
		{
			
			Class[] classes = new Class[] { 
					LifeCycleManagerWedge.class, 
					LifeCycleAdapterWedge.class, 
					ActivationWedge.class, 
					MeemStoreAdapterWedge.class,
					PersistenceHandlerAdapterWedge.class, 
					LifeCycleManagerCategoryWedge.class, 
					PersistingLifeCycleManagerWedge.class, 
					SubsystemFactoryWedge.class ,
				};
 
/*
			// TODO temporarily setup to make transient subsystems
			Class[] classes = new Class[] { 
	          TransientLifeCycleManagerWedge.class,
	          LifeCycleManagerCategoryWedge.class,
	          LifeCycleManagerWedge.class,
	          LifeCycleAdapterWedge.class,
	          SubsystemFactoryWedge.class 
			};
*/          
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(classes);

			MeemAttribute meemAttribute = new MeemAttribute(SubsystemFactory.spi.getIdentifier());

			meemDefinition.setMeemAttribute(meemAttribute);
		}

		return meemDefinition;
	}
}
