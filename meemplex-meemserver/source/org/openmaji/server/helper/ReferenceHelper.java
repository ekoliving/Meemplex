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

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.filter.FacetDescriptor;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.wedge.reference.ContentClient;

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
 * @version 1.0
 */

public class ReferenceHelper {
	/*
	 * PigeonHole Timeout value
	 */
	public static final String PIGEONHOLE_TIMEOUT = "org.openmaji.server.pigeonhole.timeout";

	private static final long timeout = Long.parseLong(System.getProperty(PIGEONHOLE_TIMEOUT, "30000"));

	/** Logger for the class */
	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * Get the proxied inbound facet for the Meem.
	 *  
	 * @param meem
	 * @param facetIdentifier
	 * @param specification
	 * @return
	 */
	public static <T extends Facet> T getTarget(Meem meem, String facetIdentifier, Class<T> specification) {
		PigeonHole<T> pigeonHole = new PigeonHole<T>();
		MeemClientImpl<T> meemClient = new MeemClientImpl<T>(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(meemClient, MeemClient.class);
		Filter filter = new FacetDescriptor(facetIdentifier, specification);
		Reference targetReference = Reference.spi.create("meemClientFacet", proxy, true, filter);

		meem.addOutboundReference(targetReference, true);

		try {
			return pigeonHole.get(timeout);
		}
		catch (TimeoutException ex) {
			logger.log(Level.INFO, "Timeout waiting for Reference", ex);
			return null;
		}
		finally {
			GatewayManagerWedge.revokeTarget(proxy, meemClient);
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
	public static <T extends Facet> void getTarget(Meem meem, String facetIdentifier, Class<T> specification, AsyncCallback<T> callback) {
		MeemClientCallback<T> meemClient = new MeemClientCallback<T>(callback);
		meemClient.receive(meem, facetIdentifier, specification);
	}

	
	public static class MeemClientCallback<T extends Facet> implements MeemClient, ContentClient {
		private AsyncCallback<T> callback;
		private MeemClient clientProxy;
		private T result = null;

		public MeemClientCallback(AsyncCallback<T> callback) {
			this.callback = callback;
		}

		public void receive(Meem meem, String facetIdentifier, Class<T> specification) {
			clientProxy = GatewayManagerWedge.getTargetFor(this, MeemClient.class);
			Filter filter = new FacetDescriptor(facetIdentifier, specification);
			Reference targetReference = Reference.spi.create("meemClientFacet", clientProxy, true, filter);

			meem.addOutboundReference(targetReference, true);

			Runnable doTimeout = new Runnable() {
				public void run() {
					logger.log(Level.INFO, "Timeout waiting for Reference");
					revokeTarget();
				}
			};
			ThreadManager.spi.create().queue(doTimeout, timeout);
		}

		public void referenceAdded(Reference reference) {
			result = reference.getTarget();
		}

		public void referenceRemoved(Reference reference) {
			result = null;
		}

		public void contentSent() {
			if (callback != null) {
				callback.result(result);
				callback = null;
				revokeTarget();
			}
		}

		public void contentFailed(String reason) {
			if (callback != null) {
				callback.exception(new Exception(reason));
				callback = null;
				revokeTarget();
			}
		}

		private void revokeTarget() {
			if (clientProxy != null) {
				GatewayManagerWedge.revokeTarget(clientProxy, this);
				clientProxy = null;
			}
		}
	}

	public static class MeemClientImpl<T> implements MeemClient, ContentClient {
		private PigeonHole<T> pigeonHole;
		private T result = null;

		public MeemClientImpl(PigeonHole<T> pigeonHole) {
			this.pigeonHole = pigeonHole;
		}

		public void referenceAdded(Reference reference) {
			result = reference.getTarget();
		}

		public void referenceRemoved(Reference reference) {
			result = null;
		}

		public void contentSent() {
			if (pigeonHole != null) {
				pigeonHole.put(result);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason) {
			if (pigeonHole != null) {
				pigeonHole.put(null);
				pigeonHole = null;
			}
		}
	}
}
