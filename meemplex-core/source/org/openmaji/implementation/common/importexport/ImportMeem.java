/*
 * @(#)ImportMeem.java
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

public class ImportMeem implements MeemDefinitionProvider {

  private MeemDefinition meemDefinition = null;

  /**
   * Return a MeemDefinition for this Meem which lists all of the
   * Wedges required to assemble an <code>ImportMeem</code>.
   * 
   * @return Import Meem MeemDefinition
	 */

	public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      Class[] wedges = new Class[] {
        ImportParametersWedge.class,
        FileImportWedge.class,
        InjectMeemWedge.class
      };

      meemDefinition =
        MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("Import");
    }

    return(meemDefinition);
  }
}