/*
 * @(#)MeemRegistryGatewayJiniLookupWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.manager.registry.jini;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookup;
import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient;
import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.meem.FacetItem;




public class JiniMeemRegistryLookupWedge implements Wedge, JiniLookupClient {

	private static final Logger logger = Logger.getAnonymousLogger();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	public JiniLookup jiniLookupConduit;
	public JiniLookupClient jiniLookupClientConduit = this;

	public void commence() {
		
		FacetItem facetItem = new FacetItem("jiniMeemRegistry", JiniMeemRegistry.class.getName(), Direction.INBOUND);
		
		jiniLookupConduit.startLookup(facetItem, false);
		
		logger.log(Level.INFO, "JiniMeemRegistry Jini lookup initiated ...");

	}

	public void meemAdded(Meem meem) {
		if (!meem.getMeemPath().equals(EssentialMeemHelper.getEssentialMeem(JiniMeemRegistry.spi.getIdentifier()).getMeemPath())) {
			logger.log(Level.INFO, "JiniMeemRegistry located: " + meem);
			JiniMeemRegistryGatewayWedge.addRemoteRegistry(meem);
		}
	}

	public void meemRemoved(Meem meem) {
		if (!meem.getMeemPath().equals(EssentialMeemHelper.getEssentialMeem(JiniMeemRegistry.spi.getIdentifier()).getMeemPath())) {
			logger.log(Level.INFO, "JiniMeemRegistry removed: " + meem);
			JiniMeemRegistryGatewayWedge.removeRemoteRegistry(meem);
		}
	}

	
}
