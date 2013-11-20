/*
 * @(#)SubsystemMeem.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;

/**
 * @author Peter
 */
public class SubsystemMeem implements MeemDefinitionProvider {
	
	public static final String LICENSE_SUBSYSTEM_ENABLED = "org.openmaji.server.licensing.enabled";

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		/*
		String licensingEnabledString = System.getProperty(LICENSE_SUBSYSTEM_ENABLED, "true");
		boolean licensingEnabled = Boolean.valueOf(licensingEnabledString).booleanValue();
		*/
		
		if (meemDefinition == null) {
			@SuppressWarnings("unchecked")
			Class<? extends Wedge>[] wedges = new Class[] { 
					LifeCycleManagerWedge.class, 
					LifeCycleAdapterWedge.class, 
					ActivationWedge.class, 
					MeemStoreAdapterWedge.class,
					PersistenceHandlerAdapterWedge.class, 
					LifeCycleManagerCategoryWedge.class, 
					PersistingLifeCycleManagerWedge.class, 
					SubsystemWedge.class,
					GenericSubsystemWedge.class 
				};
			
			/*
			if (licensingEnabled) {
				wedges = new Class[] {};
				wedges = new Class[] { 
						LicensedLifeCycleManagerWedge.class, 
						LifeCycleAdapterWedge.class, 
						ActivationWedge.class, 
						MeemStoreAdapterWedge.class,
						PersistenceHandlerAdapterWedge.class, 
						LifeCycleManagerCategoryWedge.class, 
						PersistingLifeCycleManagerWedge.class, 
						SubsystemWedge.class,
						GenericSubsystemWedge.class, 
						LicensingWedge.class, 
						SessionMeemFactoryClientWedge.class 
					};
			}
			else {
				wedges = new Class[] { 
						LifeCycleManagerWedge.class, 
						LifeCycleAdapterWedge.class, 
						ActivationWedge.class, 
						MeemStoreAdapterWedge.class,
						PersistenceHandlerAdapterWedge.class, 
						LifeCycleManagerCategoryWedge.class, 
						PersistingLifeCycleManagerWedge.class, 
						SubsystemWedge.class,
						GenericSubsystemWedge.class 
					};
			}
		*/

			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
			meemDefinition.setMeemAttribute(new MeemAttribute(Subsystem.spi.getIdentifier()));
		}

		return meemDefinition;
	}
}
