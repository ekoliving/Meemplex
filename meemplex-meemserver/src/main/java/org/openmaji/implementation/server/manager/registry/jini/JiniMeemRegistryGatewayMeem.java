/*
 * @(#)PersistingLifeCycleManagerMeem.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry.jini;

import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupWedge;
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
public class JiniMeemRegistryGatewayMeem implements MeemDefinitionProvider {

	private static MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {

			MeemDefinitionFactory meemDefinitionFactory = MeemDefinitionFactory.spi.create();
			
			meemDefinition = meemDefinitionFactory.createMeemDefinition(
				new Class[] {
					JiniMeemRegistryGatewayWedge.class,
					JiniMeemRegistryLookupWedge.class,
					JiniLookupWedge.class,
					JiniMeemRegistryExportWedge.class
				}
			);

		}
		return meemDefinition;
	}
	
	public static class spi {
		public static String getIdentifier() {
			return "jiniMeemRegistryGateway";
		}
	}
	
}
