/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

import org.openmaji.implementation.common.BinaryWedge;

public class LoopbackTrueBinaryMeem implements MeemDefinitionProvider
{
  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition()
  {
    if ( meemDefinition == null )
    {
      Class[] wedges = new Class[2];
      wedges[0] = BinaryWedge.class;
      wedges[1] = LoopbackTrueBinaryWedge.class;
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("LoopbackTrueBinary");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"BinaryWedge","binary","binaryInput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"BinaryWedge","binaryClient","binaryOutput");
    }

    return meemDefinition;
  }
}
