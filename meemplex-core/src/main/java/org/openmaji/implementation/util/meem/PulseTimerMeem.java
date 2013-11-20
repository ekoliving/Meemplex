/*
 * Copyright 2008 (c) by ekoLiving, Pty. Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of ekoLiving Pty. Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.util.meem;

import org.openmaji.meem.definition.*;

import org.openmaji.implementation.util.wedge.PulseTimerWedge;

public class PulseTimerMeem implements MeemDefinitionProvider {

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      Class<?>[] wedges = new Class<?>[] {
        PulseTimerWedge.class,
      };

      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("PulseTimer");

      MeemDefinitionUtility.renameFacetIdentifier(
        meemDefinition, "PulseTimerWedge", "unary", "unaryInput"
      );
    }

    return(meemDefinition);
  }
}