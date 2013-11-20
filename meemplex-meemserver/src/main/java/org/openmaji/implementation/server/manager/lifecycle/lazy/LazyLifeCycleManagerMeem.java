/*
 * @(#)LazyLifeCycleManagerMeem.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.lazy;

import java.util.*;

import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle.LifeCycleAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryWedge;


import org.openmaji.meem.definition.*;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.meem.definition.DefinitionFactory;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LazyLifeCycleManagerMeem {

	private static MeemDefinition meemDefinition = null;

	public static MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {

			DefinitionFactory definitionFactory = DefinitionFactory.spi.create();
			
			List wedges = new ArrayList();
			
			wedges.add(LifeCycleManagerWedge.class);
			wedges.add(LifeCycleAdapterWedge.class);			
			wedges.add(ActivationWedge.class);
			wedges.add(MeemStoreAdapterWedge.class);
			wedges.add(PersistenceHandlerAdapterWedge.class);
			wedges.add(LifeCycleManagerCategoryWedge.class);
			wedges.add(LazyLifeCycleManagerWedge.class);
			
			meemDefinition = definitionFactory.createMeemDefinition(wedges.iterator());

			MeemAttribute meemAttribute = new MeemAttribute(LifeCycleManager.spi.getIdentifier());
			
			meemDefinition.setMeemAttribute(meemAttribute);


		}
		return meemDefinition;
	}
}