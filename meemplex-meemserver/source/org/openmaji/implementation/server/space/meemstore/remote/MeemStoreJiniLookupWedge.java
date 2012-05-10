/*
 * @(#)MeemStoreJiniLookupWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.space.meemstore.remote;


import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookup;
import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient;
import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.space.meemstore.MeemStore;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


public class MeemStoreJiniLookupWedge implements Wedge, JiniLookupClient {

	private static final Logger logger = LogFactory.getLogger();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public JiniLookup jiniLookupConduit;
	public JiniLookupClient jiniLookupClientConduit = this;

	public void commence() {

		FacetItem facetItem = new FacetItem("meemStore", MeemStore.class.getName(), Direction.INBOUND);

		jiniLookupConduit.startLookup(facetItem, true);

		LogTools.info(logger, "MeemStore Jini lookup initiated ...");

	}

	public void conclude() {
		jiniLookupConduit.stopLookup();
	}

	public void meemAdded(Meem meem) {
		LogTools.info(logger, "... MeemStore located: " + meem.getMeemPath());

		MeemStoreProxyWedge.setMeemStore(meem);
	}

	public void meemRemoved(Meem meem) {
		LogTools.info(logger, "... MeemStore removed: " + meem.getMeemPath());

		MeemStoreProxyWedge.setMeemStore(null);
	}

}