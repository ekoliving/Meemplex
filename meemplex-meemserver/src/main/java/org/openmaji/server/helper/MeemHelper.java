/*
 * @(#)MeemHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.FacetCallbackTask;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentException;


import java.util.logging.Level;
import java.util.logging.Logger;

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

		final PigeonHole<Boolean> pigeonHole = new PigeonHole<Boolean>();

//		new FacetClientTask(meem, "facetClientFacet", filter, pigeonHole);
		
		FacetClient facetClient = new FacetClientCallback(pigeonHole);
		
		// get a non-blocking target
		FacetClient proxy = GatewayManagerWedge.getTargetFor(facetClient, FacetClient.class);
		final Reference<FacetClient> meemReference = Reference.spi.create("facetClientFacet", proxy, true, filter);
		meem.addOutboundReference(meemReference, true);

		try
		{
			Boolean found = pigeonHole.get();
			return found.booleanValue();
		}
		catch (ContentException e)
		{
			logger.log(Level.INFO, "Content exception waiting for content for meemPath: " + meem.getMeemPath(), e);
			return false;
		}
		catch (TimeoutException e)
		{
			logger.log(Level.INFO, "Timeout waiting for content for meemPath: " + meem.getMeemPath(), e);
			return false;			
		}
	}

	
	private static class FacetClientTask extends FacetCallbackTask<FacetClient, Boolean>{
		public FacetClientTask(Meem meem, String facetName, Filter filter, AsyncCallback<Boolean> callback) {
			super(meem, facetName, filter, callback);
		}
		
		public FacetClientTask(Meem meem, String facetName, Filter filter, PigeonHole<Boolean> pigeonHole) {
			super(meem, facetName, filter, pigeonHole);
		}
		
		protected FacetClient getFacetListener() {
			return new FacetClientListener();
		}
		
		private class FacetClientListener extends FacetListener implements FacetClient {
			public void facetsAdded(FacetItem[] facetItems) {
				boolean found = (facetItems != null && facetItems.length > 0);
				setResult(found);
			}
			public void facetsRemoved(FacetItem[] facetItems) {
			}
		}
	}
	
	public static class FacetClientCallback implements FacetClient, ContentClient {

		public FacetClientCallback(PigeonHole<Boolean> pigeonHole)
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
//			logger.log(Level.INFO, "Content sent");
			if (pigeonHole != null)
			{
				pigeonHole.put(found);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason)
		{
			logger.log(Level.INFO, "Content failed: " + reason);
			if (pigeonHole != null)
			{
				pigeonHole.put(Boolean.FALSE);
				pigeonHole = null;
			}
		}

		private PigeonHole<Boolean> pigeonHole;
		private Boolean found = Boolean.FALSE;
	}

	/** Logger for the class */
	private static final Logger logger = Logger.getAnonymousLogger();
}
