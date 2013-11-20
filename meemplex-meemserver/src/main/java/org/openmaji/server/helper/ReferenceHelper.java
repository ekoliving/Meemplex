/*
 * @(#)ReferenceHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Determine how to make a "data driven" synchronous Helper method.
 */

package org.openmaji.server.helper;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.filter.FacetDescriptor;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.FacetCallbackTask;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.meem.wedge.reference.ContentException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @author Warren Bloomer
 * @version 1.0
 */

public class ReferenceHelper {

	/**
	 * Get the proxied inbound facet for the Meem.
	 *  
	 * @param meem
	 * @param facetIdentifier
	 * @param specification
	 * @return
	 */
	public static <T extends Facet> T getTarget(Meem meem, String facetName, Class<T> specification) {
		final PigeonHole<T> pigeonHole = new PigeonHole<T>();

		Filter filter = new FacetDescriptor(facetName, specification);
		new GetTargetTask<T>(meem, "meemClientFacet", filter, pigeonHole);
		
		try {
			return pigeonHole.get(timeout);
		}
		catch (ContentException ex) {
			logger.log(Level.INFO, "ContentException waiting for Reference", ex);
			return null;
		}
		catch (TimeoutException ex) {
			logger.log(Level.INFO, "Timeout waiting for Reference", ex);
			return null;
		}
	}

	/**
	 * Get the proxied inbound facet for the Meem.
	 * 
	 * @param meem
	 * @param facetIdentifier
	 * @param specification
	 * @param callback
	 */
	public static <T extends Facet> void getTarget(Meem meem, String facetName, Class<T> specification, AsyncCallback<T> callback) {
		Filter filter = new FacetDescriptor(facetName, specification);
		new GetTargetTask<T>(meem, "meemClientFacet", filter, callback);
	}
	
	/**
	 * Used to get a reference asynchronously.
	 * 
	 * @param <T>
	 */	
	private static class GetTargetTask <T extends Facet> extends FacetCallbackTask<MeemClient, T> {
		public GetTargetTask(Meem meem, String facetName, Filter filter, AsyncCallback<T> callback) {
			super(meem, facetName, filter, callback);
		}

		public GetTargetTask(Meem meem, String facetName, Filter filter, PigeonHole<T> pigeonHole) {
			super(meem, facetName, filter, pigeonHole);
		}
		
		@Override
		protected MeemClient getFacetListener() {
			return (MeemClient) new MeemClientFacetListener();
		}
		
		class MeemClientFacetListener extends FacetListener implements MeemClient {
			@SuppressWarnings("unchecked")
			public void referenceAdded(Reference<?> reference) {
				setResult((T)reference.getTarget());
			}
			public void referenceRemoved(Reference<?> reference) {
				setResult(null);
			}
		}
	}

	/**
	 * PigeonHole Timeout value
	 */
	private static final long timeout = Long.parseLong(System.getProperty(PigeonHole.PROPERTY_TIMEOUT, "60000"));

	/** Logger for the class */
	private static final Logger logger = Logger.getAnonymousLogger();

}
