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


import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookup;
import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient;
import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.meem.FacetItem;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


public class JiniMeemRegistryLookupWedge implements Wedge, JiniLookupClient {

	private static final Logger logger = LogFactory.getLogger();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	public JiniLookup jiniLookupConduit;
	public JiniLookupClient jiniLookupClientConduit = this;

	public void commence() {
		
		FacetItem facetItem = new FacetItem("jiniMeemRegistry", JiniMeemRegistry.class.getName(), Direction.INBOUND);
		
		jiniLookupConduit.startLookup(facetItem, false);
		
		LogTools.info(logger, "JiniMeemRegistry Jini lookup initiated ...");

	}

	public void meemAdded(Meem meem) {
		if (!meem.getMeemPath().equals(EssentialMeemHelper.getEssentialMeem(JiniMeemRegistry.spi.getIdentifier()).getMeemPath())) {
			LogTools.info(logger, "JiniMeemRegistry located: " + meem);
			JiniMeemRegistryGatewayWedge.addRemoteRegistry(meem);
		}
	}

	public void meemRemoved(Meem meem) {
		if (!meem.getMeemPath().equals(EssentialMeemHelper.getEssentialMeem(JiniMeemRegistry.spi.getIdentifier()).getMeemPath())) {
			LogTools.info(logger, "JiniMeemRegistry removed: " + meem);
			JiniMeemRegistryGatewayWedge.removeRemoteRegistry(meem);
		}
	}

	
}
