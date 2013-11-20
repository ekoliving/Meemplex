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

public class RequestProcessorMeem implements MeemDefinitionProvider
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
    		  RequestProcessorWedge.class,
      };
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("RequestProcessor");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"RequestProcessorWedge","request","variableOutput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"RequestProcessorWedge","requestProcessor","requestProcessorInput");
    }

    return meemDefinition;
  }
}
