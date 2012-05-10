/*
 * @(#)ExportMeem.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common.importexport;

import org.openmaji.meem.definition.*;

/**
 */

public class ExportMeem implements MeemDefinitionProvider {

  private MeemDefinition meemDefinition = null;

  /**
   * Return a MeemDefinition for this Meem which lists all of the
   * Wedges required to assemble an <code>ExportMeem</code>.
   * 
   * @return Export Meem MeemDefinition
	 */

	public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      Class[] wedges = new Class[] {
        ExportParametersWedge.class,
      	CategoryTraverserWedge.class,
      	ExtractMeemWedge.class,
        FileExportWedge.class
      };

      meemDefinition =
       MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("Export");
    }

    return(meemDefinition);
  }
}