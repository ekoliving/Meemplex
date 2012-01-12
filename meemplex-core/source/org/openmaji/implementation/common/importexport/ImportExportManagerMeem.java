/*
 * @(#)ImportExportManagerMeem.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common.importexport;

import org.openmaji.meem.definition.*;

/**
 */

public class ImportExportManagerMeem implements MeemDefinitionProvider {

  private MeemDefinition meemDefinition = null;

  /**
   * Return a MeemDefinition for this Meem which lists all of the
   * Wedges required to assemble an <code>ImportExportManagerMeem</code>.
   * 
   * @return ImportExportManager Meem MeemDefinition
	 */

	public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      Class[] wedges = new Class[] {
        ImportExportManagerWedge.class
      };

      meemDefinition =
        MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("ImportExport");
    }

    return(meemDefinition);
  }
}