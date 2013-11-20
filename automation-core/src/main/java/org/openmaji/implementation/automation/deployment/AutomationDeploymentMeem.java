/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 * 
 */
 
package org.openmaji.implementation.automation.deployment;

import org.openmaji.implementation.deployment.wedge.DeploymentWedge;
import org.openmaji.implementation.deployment.wedge.ProgressClientWedge;
import org.openmaji.implementation.deployment.wedge.XmlDeploymentProcessorWedge;
import org.openmaji.implementation.deployment.wedge.XmlReaderWedge;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;

public class AutomationDeploymentMeem implements MeemDefinitionProvider
{
	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition()
	{
		Class<?>[] wedges = new Class[] { 
				XmlReaderWedge.class, 
				XmlDeploymentProcessorWedge.class, 
				DeploymentWedge.class, 
				AutomationDeploymentWedge.class, 
				ProgressClientWedge.class 
			};
		meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
		meemDefinition.getMeemAttribute().setIdentifier("AutomationDeployment");
		return meemDefinition;
	}
}
