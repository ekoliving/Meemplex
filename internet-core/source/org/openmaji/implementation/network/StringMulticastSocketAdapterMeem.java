/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.network;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;
import org.openmaji.implementation.common.VariableWedge;

/**
 * This Meem adapts a Multicast Socket to a StringVariable.
 * It implements <code>MeemDefinitionProvider</code> so that
 * <code>the MeemkitInstaller</code> can obtain a <code>MeemDefinition</code>
 * from which it can create an instance.
 *
 * @author Chris Kakris
 */

public class StringMulticastSocketAdapterMeem implements MeemDefinitionProvider
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
    		  StringMulticastSocketAdapterWedge.class,
      };
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("StringMulticastSocketAdapter");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"VariableWedge","variable","variableInput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"VariableWedge","variableClient","variableOutput");
    }

    return meemDefinition;
  }
}
