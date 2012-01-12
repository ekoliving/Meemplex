/*
 * @(#)HyperSpaceMeem.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.hyperspace;

import java.util.ArrayList;
import java.util.List;


import org.openmaji.implementation.common.VariableMapWedge;
import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle.LifeCycleAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryWedge;
import org.openmaji.implementation.server.manager.lifecycle.lazy.LazyLifeCycleManagerWedge;
import org.openmaji.implementation.server.space.CategoryWedge;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.meem.definition.DefinitionFactory;
import org.openmaji.system.space.hyperspace.HyperSpace;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class HyperSpaceMeem {
	
	private static final Logger logger = LogFactory.getLogger();

	private static final String ACTIVATION_TYPE = "org.openmaji.server.manager.lifecycle.hyperspace.lifecyclemanager";
	private static MeemDefinition meemDefinition = null;
	
	public static MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			Class lifeCycleManager = LazyLifeCycleManagerWedge.class;
			 
			String lifeCycleManagerProperty = System.getProperty(ACTIVATION_TYPE);
			if (lifeCycleManagerProperty != null) {
				try {
					lifeCycleManager = Class.forName(lifeCycleManagerProperty.trim());
					if (!LifeCycleManager.class.isAssignableFrom(lifeCycleManager)) {
						LogTools.error(logger, "Class: [" + lifeCycleManagerProperty + "] doesn't implement LifeCycleManager. Using LazyLifeCycleManagerWedge instead.");
						lifeCycleManager = LazyLifeCycleManagerWedge.class;
					}
				} catch (ClassNotFoundException e) {
					LogTools.error(logger, "Error loading class: [" + lifeCycleManagerProperty + "]. Using LazyLifeCycleManagerWedge instead.");
					lifeCycleManager = LazyLifeCycleManagerWedge.class;
				}
			}
						
			DefinitionFactory definitionFactory = DefinitionFactory.spi.create();
			
			List wedges = new ArrayList();
			
			wedges.add(LifeCycleManagerWedge.class);
			wedges.add(LifeCycleAdapterWedge.class);			
			wedges.add(ActivationWedge.class);
			wedges.add(MeemStoreAdapterWedge.class);
			wedges.add(PersistenceHandlerAdapterWedge.class);
			wedges.add(LifeCycleManagerCategoryWedge.class);
			wedges.add(lifeCycleManager);
			wedges.add(CategoryWedge.class);
			wedges.add(HyperSpaceLifeCycleManagerWedge.class);
			wedges.add(VariableMapWedge.class);
			
			meemDefinition = definitionFactory.createMeemDefinition(wedges.iterator());

			MeemAttribute meemAttribute = new MeemAttribute(HyperSpace.spi.getIdentifier());
			
			meemDefinition.setMeemAttribute(meemAttribute);

		}
		return meemDefinition;
	}
	
}
