/*
 * @(#)HyperSpaceJiniLookupMeem.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.hyperspace.remote;

import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupWedge;
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
public class HyperSpaceJiniLookupMeem implements MeemDefinitionProvider {
	
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {
          HyperSpaceJiniLookupWedge.class,
          JiniLookupWedge.class
        }
      );
    }
    
    return(meemDefinition);
  }

}
