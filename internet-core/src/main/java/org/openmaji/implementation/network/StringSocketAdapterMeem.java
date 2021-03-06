/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.network;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;
import org.openmaji.implementation.common.VariableWedge;

/**
 * This Meem adapts a Socket to a StringVariable.
 * It implements <code>MeemDefinitionProvider</code> so that
 * <code>the MeemkitInstaller</code> can obtain a <code>MeemDefinition</code>
 * from which it can create an instance.
 *
 * @author Chris Kakris
 */

public class StringSocketAdapterMeem implements MeemDefinitionProvider
{
  private MeemDefinition meemDefinition = null;

  /**
   * Return a MeemDefinition for this Meem which lists all of the
   * Wedges required to assemble this Meem.
   * 
   * @return The MeemDefinition for this Meem
   * 
	 */
	public MeemDefinition getMeemDefinition()
  {
    if ( meemDefinition == null )
    {
      Class<?>[] wedges = new Class[] {
    		  VariableWedge.class,
    		  StringSocketAdapterWedge.class,
      };
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("StringSocketAdapter");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"VariableWedge","variable","variableInput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"VariableWedge","variableClient","variableOutput");
    }

    return meemDefinition;
  }
}
