/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 * 
 */
 
package org.openmaji.implementation.deployment.meem;

import org.openmaji.implementation.deployment.wedge.DeploymentWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;


/**
 * 
 * Creates Meems for StVs application.
 * 
 * @author Warren Bloomer
 *
 */
public class DeploymentMeem implements MeemDefinitionProvider
{
	private MeemDefinition meemDefinition = null;

	/**
	 * Return a MeemDefinition for this Meem.
	 */
	public MeemDefinition getMeemDefinition() {
		if ( meemDefinition == null ) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] {DeploymentWedge.class});
      meemDefinition.getMeemAttribute().setIdentifier("Deployment");
		}

		return meemDefinition;
	}
}
