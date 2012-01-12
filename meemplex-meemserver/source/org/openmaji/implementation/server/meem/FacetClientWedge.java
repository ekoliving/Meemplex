/*
 * @(#)FacetClientWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meem;

import org.openmaji.implementation.server.meem.core.MeemCoreImpl;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.filter.FacetFilter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.meem.FacetClientCallback;
import org.openmaji.system.meem.FacetClientConduit;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.*;

/**
 *
 */
public class FacetClientWedge implements Wedge {
	public MeemCore meemCore;

	public FacetClientConduit facetClientConduit = new FacetClientImpl();

	private final class FacetClientImpl implements FacetClientConduit {
		/**
		 */
		public void hasA(Meem meem, String facetIdentifier, Class<? extends Facet> specification, Direction direction, FacetClientCallback callback) {
			if (meem.equals(meemCore.getSelf())) {
				FacetImpl facetImpl = ((MeemCoreImpl) meemCore).getFacetImpl(facetIdentifier, direction);

				callback.facetExists(facetImpl != null && specification.isInstance(facetImpl));

				return;
			}

			FacetFilter filter = new FacetFilter(facetIdentifier, specification, direction);

			CallbackClient client = new CallbackClient(callback);

			FacetClient facetClientProxy = (FacetClient) meemCore.getLimitedTargetFor(client, FacetClient.class);

			Reference meemReference = Reference.spi.create("facetClientFacet", facetClientProxy, true, filter);

			meem.addOutboundReference(meemReference, true);
		}
	}

	public static final class CallbackClient implements FacetClient, ContentClient {
		FacetClientCallback callback;

		boolean found = false;

		public CallbackClient(FacetClientCallback callback) {
			this.callback = callback;
		}

//		public void hasA(String facetIdentifier, Class<? extends Facet> specification, Direction direction) {
//			found = true;
//		}
		
		public void facetsAdded(FacetItem[] facetItems) {
			found = (facetItems != null && facetItems.length > 0);
		}
		public void facetsRemoved(FacetItem[] facetItems) {
		}
		
		public void contentSent() {
			callback.facetExists(found);
		}

		public void contentFailed(String reason) {
			callback.facetExists(false);
		}
	}
}
