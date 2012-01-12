/*
 * @(#)ReferenceHelper.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
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
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class ReferenceHelper
{
	/*
	 * PigeonHole Timeout value
	 */
	public static final String PIGEONHOLE_TIMEOUT = "org.openmaji.server.pigeonhole.timeout";
	
	private static final long timeout = Long.parseLong(System.getProperty(PIGEONHOLE_TIMEOUT, "30000"));

	/** Logger for the class */
	private static final Logger logger = LogFactory.getLogger();
	
	public static <T extends Facet> T getTarget(Meem meem, String facetIdentifier, Class<T> specification)
	{
		PigeonHole<T> pigeonHole = new PigeonHole<T>();
		MeemClientImpl<T> meemClient = new MeemClientImpl<T>(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(meemClient, MeemClient.class);
		Filter filter = new FacetDescriptor(facetIdentifier, specification);  
		Reference targetReference = Reference.spi.create("meemClientFacet", proxy, true, filter);

		meem.addOutboundReference(targetReference, true);
		
		try
		{
			return pigeonHole.get(timeout);
		}
		catch (TimeoutException ex)
		{
			LogTools.info(logger, "Timeout waiting for Reference", ex);
			return null;
		}
		finally
		{
			GatewayManagerWedge.revokeTarget(proxy, meemClient);
		}
	}

	public static class MeemClientImpl <T> implements MeemClient, ContentClient
	{
		private PigeonHole<T> pigeonHole;
		private T result = null;
		
		public MeemClientImpl(PigeonHole<T> pigeonHole)
		{
			this.pigeonHole = pigeonHole;
		}

		public void referenceAdded(Reference reference)
		{
			result = reference.getTarget();
		}

		public void referenceRemoved(Reference reference)
		{
			result = null;
		}

		public void contentSent()
		{
			if (pigeonHole != null)
			{
				pigeonHole.put(result);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason)
		{
			if (pigeonHole != null)
			{
				pigeonHole.put(null);
				pigeonHole = null;
			}
		}
	}
}

