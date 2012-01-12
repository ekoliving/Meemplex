/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 * 
 */

package org.openmaji.implementation.deployment.meem;


import org.openmaji.implementation.deployment.wedge.DeploymentSubsystemWedge;
import org.openmaji.implementation.diagnostic.DebugWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerDefinitionFactory;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;

public class DeploymentSubsystemMeem implements MeemDefinitionProvider {
	private MeemDefinition meemDefinition = null;

	/**
	 * Return a MeemDefinition for this Meem.
	 */
	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			Class<?>[] wedges = new Class[] { 
					DeploymentSubsystemWedge.class, 
					Subsystem.spi.create().getClass(), 
					DebugWedge.class 
			};
			meemDefinition = LifeCycleManagerDefinitionFactory.spi.get().createPersisting(wedges);
			meemDefinition.getMeemAttribute().setIdentifier("Deployment Subsystem");
		}

		return meemDefinition;
	}
}
