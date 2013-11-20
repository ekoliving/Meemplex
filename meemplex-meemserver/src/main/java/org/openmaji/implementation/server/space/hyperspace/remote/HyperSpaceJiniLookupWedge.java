/*
 * @(#)HyperSpaceJiniLookupWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.space.hyperspace.remote;


import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookup;
import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient;
import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.server.helper.HyperSpaceHelper;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.space.hyperspace.HyperSpace;


import java.util.logging.Level;
import java.util.logging.Logger;


public class HyperSpaceJiniLookupWedge implements Wedge, JiniLookupClient {

	private static final Logger logger = Logger.getAnonymousLogger();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public JiniLookup jiniLookupConduit;
	public JiniLookupClient jiniLookupClientConduit = this;

	public void commence() {
		FacetItem facetItem = new FacetItem("hyperSpace", HyperSpace.class.getName(), Direction.INBOUND);

		jiniLookupConduit.startLookup(facetItem, true);

		logger.log(Level.INFO, "HyperSpace Jini lookup initiated ...");
	}

	public void conclude() {
		jiniLookupConduit.stopLookup();
	}

	public void meemAdded(Meem meem) {
		logger.log(Level.INFO, "... HyperSpace located: " + meem.getMeemPath());

		HyperSpaceHelper.getInstance().setHyperSpaceMeem(meem);
	}

	public void meemRemoved(Meem meem) {
		logger.log(Level.INFO, "... HyperSpace removed");

		HyperSpaceHelper.getInstance().setHyperSpaceMeem(null);
	}

}