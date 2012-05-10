/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.diagnostic;


import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;

public class LifeCycleTransitionLoggerMeem implements MeemDefinitionProvider
{
	public MeemDefinition getMeemDefinition()
  {
    Class[] wedges = new Class[] { LifeCycleTransitionLoggerWedge.class };
    MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
    meemDefinition.getMeemAttribute().setIdentifier("LifeCycleTransitionLogger");
    return meemDefinition;
  }
}
