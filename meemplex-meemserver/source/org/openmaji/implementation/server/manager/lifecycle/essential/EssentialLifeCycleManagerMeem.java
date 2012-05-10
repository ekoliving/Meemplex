/*
 * @(#)EssentialLifeCycleManagerMeem.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
 
package org.openmaji.implementation.server.manager.lifecycle.essential;

import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle.LifeCycleAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryWedge;
import org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerWedge;

import org.openmaji.meem.definition.*;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;



/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */

public class EssentialLifeCycleManagerMeem implements MeemDefinitionProvider {
  
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  public MeemDefinition getMeemDefinition() {
    return (
      MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {
          LifeCycleManagerWedge.class,
          LifeCycleAdapterWedge.class,      
          ActivationWedge.class,
          MeemStoreAdapterWedge.class,
          PersistenceHandlerAdapterWedge.class,
          LifeCycleManagerCategoryWedge.class,
          PersistingLifeCycleManagerWedge.class,
          EssentialLifeCycleManagerWedge.class
        }
      )
    );
  }

/* ---------- Nested class for SPI ----------------------------------------- */

  public static class spi {
    public static String getIdentifier() {
      return(EssentialLifeCycleManager.spi.getIdentifier());
    };
  }
}