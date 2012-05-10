/*
 * Copyright 2006 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;
import org.openmaji.implementation.automation.common.DeviceWedge;
import org.openmaji.implementation.common.LinearWedge;

public class SlowLoopbackLinearMeem implements MeemDefinitionProvider
{
  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition()
  {
    if ( meemDefinition == null )
    {
      Class[] wedges = new Class[] { LinearWedge.class, DeviceWedge.class, SlowLoopbackLinearWedge.class };
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("SlowLoopbackLinear");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"DeviceWedge","device","deviceInput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"LinearWedge","linear","linearInput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"LinearWedge","linearClient","linearOutput");
    }

    return meemDefinition;
  }
}
