/*
 * @(#)MeemHelper.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.server.helper;


import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.filter.FacetFilter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemHelper
{
	public static boolean isA(Meem meem, Class<? extends Facet> specification)
	{
		FacetFilter filter = new FacetFilter(specification, Direction.INBOUND);

		return checkFacet(meem, filter);
	}

	public static boolean hasA(Meem meem, String facetIdentifier, Class<? extends Facet> specification, Direction direction) {

		FacetFilter filter = new FacetFilter(facetIdentifier, specification, direction);

		return checkFacet(meem, filter);
	}

	private static boolean checkFacet(final Meem meem, FacetFilter filter)
	{
		if (meem == null)
		{
			throw new RuntimeException("meem cannot be null");
		}

		PigeonHole pigeonHole = new PigeonHole();
		FacetClient facetClient = new FacetClientCallback(pigeonHole);
		
		// get a non-blocking target
		Facet proxy = GatewayManagerWedge.getTargetFor(facetClient, FacetClient.class);
		
		final Reference meemReference = Reference.spi.create("facetClientFacet", proxy, true, filter);

		meem.addOutboundReference(meemReference, true);

		try
		{
			Boolean found = (Boolean) pigeonHole.get(timeout);

			return found.booleanValue();
		}
		catch (TimeoutException e)
		{
			LogTools.info(logger, "Timeout waiting for content for meemPath: " + meem.getMeemPath(), e);
			return false;			
		}
		finally
		{
			GatewayManagerWedge.revokeTarget(proxy, facetClient);
		}
	}

	public static class FacetClientCallback implements FacetClient, ContentClient {

		public FacetClientCallback(PigeonHole pigeonHole)
		{
			this.pigeonHole = pigeonHole;
		}

//		public void hasA(String facetIdentifer, Class<? extends Facet> specification, Direction direction)
//		{
//			this.found = Boolean.TRUE;
//		}
		
		public void facetsAdded(FacetItem[] facetItems) {
			this.found = (facetItems != null && facetItems.length > 0);
		}
		
		public void facetsRemoved(FacetItem[] facetItems) {
		}
		

		public void contentSent()
		{
//			LogTools.info(logger, "Content sent");
			if (pigeonHole != null)
			{
				pigeonHole.put(found);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason)
		{
			LogTools.info(logger, "Content failed: " + reason);
			if (pigeonHole != null)
			{
				pigeonHole.put(Boolean.FALSE);
				pigeonHole = null;
			}
		}

		private PigeonHole pigeonHole;
		private Boolean found = Boolean.FALSE;
	}

	private static final long timeout = 60000;

	/** Logger for the class */
	private static final Logger logger = LogFactory.getLogger();
}
