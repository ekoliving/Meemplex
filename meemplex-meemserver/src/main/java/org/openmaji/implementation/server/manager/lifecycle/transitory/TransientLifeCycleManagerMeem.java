/*
 * @(#)TransientLifeCycleManagerMeem.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.transitory;

import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle.LifeCycleAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;



/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */

public class TransientLifeCycleManagerMeem implements MeemDefinitionProvider {
	
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  public MeemDefinition getMeemDefinition() {
    return (
      MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {
          TransientLifeCycleManagerWedge.class,
          LifeCycleManagerCategoryWedge.class,
          LifeCycleManagerWedge.class,
          LifeCycleAdapterWedge.class
        }
      )
    );
  }
  
/* ---------- Nested class for SPI ----------------------------------------- */

  public static class spi {
    public static String getIdentifier() {
      return("transientLifeCycleManager");
    };
  }
}