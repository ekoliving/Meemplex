/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.system.meemkit.core.MeemkitManager;



public class MeemkitManagerMeem implements MeemDefinitionProvider
{
  /* --------- MeemDefinitionProvider method(s) ----------------------------- */

  public MeemDefinition getMeemDefinition()
  {
    Class<?>[] wedges = new Class[] { 
    		MeemkitManagerWedge.class, 
    		MeemPatternInstallerWedge.class, 
    		MeemkitClassloaderMonitorWedge.class, 
    		JiniLookupWedge.class 
    	};
    
    MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
    meemDefinition.getMeemAttribute().setIdentifier(MeemkitManager.spi.getIdentifier());
    return meemDefinition;
  }
}
