/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
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

public class TransientDeploymentSubsystemMeem implements MeemDefinitionProvider {
	public MeemDefinition getMeemDefinition() {
		Class[] wedges = new Class[] { 
				DeploymentSubsystemWedge.class, 
				Subsystem.spi.create().getClass(), 
				DebugWedge.class, 
			};

		MeemDefinition meemDefinition = LifeCycleManagerDefinitionFactory.spi.get().createTransient(wedges);
		meemDefinition.getMeemAttribute().setIdentifier("Transient Deployment Subsystem");

		return meemDefinition;
	}
}
