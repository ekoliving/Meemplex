/*
 * @(#)MeemServerControllerMeem.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meemserver.controller;

import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupWedge;
import org.openmaji.implementation.server.space.CategoryWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;



/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class MeemServerControllerMeem implements MeemDefinitionProvider {
	
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {
        	MeemServerControllerWedge.class,
          JiniMeemServerControllerWedge.class,
          JiniLookupWedge.class,
          CategoryWedge.class
        }
      );
    }
    
    return(meemDefinition);
  }

}
