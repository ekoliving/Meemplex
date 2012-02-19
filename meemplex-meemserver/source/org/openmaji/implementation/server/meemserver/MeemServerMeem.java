/*
 * @(#)MeemServerMeem.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meemserver;

import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle.LifeCycleAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryWedge;
import org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerWedge;
import org.openmaji.implementation.server.space.CategoryWedge;

import org.openmaji.meem.definition.*;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class MeemServerMeem implements MeemDefinitionProvider {
	
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {
          LifeCycleManagerWedge.class,
          LifeCycleAdapterWedge.class,
          ActivationWedge.class,
          MeemStoreAdapterWedge.class,
          PersistenceHandlerAdapterWedge.class,
          LifeCycleManagerCategoryWedge.class,
          PersistingLifeCycleManagerWedge.class,
          MeemServerWedge.class,
          CategoryWedge.class
        }
      );
			
			// -mg- Ideally hide the LifeCycleManager facet from definition here

    }
    return(meemDefinition);
  }

}
