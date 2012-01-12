/*
 * @(#)SearchManagerClientHelper.java
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
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.resolver.MeemResolver;
import org.openmaji.system.space.resolver.MeemResolverClient;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * @author mg
 * Created on 20/01/2003
 */
public class MeemPathResolverHelper
{
	private static MeemPathResolverHelper instance = new MeemPathResolverHelper();

	private MeemPathResolverHelper()
	{}

	public static MeemPathResolverHelper getInstance()
	{
		return instance;
	}

	public Meem resolveMeemPath(final MeemPath meemPath)
	{		
		return meemPath.isDefinitive()
			?	resolveDefinitive(meemPath)
			:	resolveHyperSpace(meemPath);
	}

	private Meem resolveDefinitive(MeemPath meemPath)
	{
		PigeonHole pigeonHole = new PigeonHole();

		MeemRegistryClient meemRegistryClient = new MeemRegistryClientImpl(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(meemRegistryClient, MeemRegistryClient.class);

		Filter filter = new ExactMatchFilter(meemPath);

		Reference reference = Reference.spi.create("meemRegistryClient", proxy, true, filter);

		Meem meemRegistryGateway = EssentialMeemHelper.getEssentialMeem(
			MeemRegistryGateway.spi.getIdentifier());

		meemRegistryGateway.addOutboundReference(reference, true);

		try
		{
			return (Meem) pigeonHole.get(timeout);
		}
		catch (TimeoutException ex)
		{
			LogTools.info(logger, "Timeout waiting for Meem for the MeemPath", ex);
			return null;
		}
		finally
		{
			GatewayManagerWedge.revokeTarget(proxy, meemRegistryClient);
		}
	}
	
	private Meem resolveHyperSpace(MeemPath meemPath)
	{
		PigeonHole pigeonHole = new PigeonHole();
		final MeemPathResolverClientImpl client = new MeemPathResolverClientImpl(pigeonHole);

		Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());
		Facet proxy = GatewayManagerWedge.getTargetFor(client, MeemResolverClient.class);

		Reference reference = Reference.spi.create(
				"meemResolverClient",
				proxy,
				true,
				new ExactMatchFilter(meemPath));

		resolverMeem.addOutboundReference(reference, true);

		try
		{
			return (Meem) pigeonHole.get(timeout);
		}
		catch (TimeoutException ex)
		{
			LogTools.info(logger, "Timeout waiting for Meem for the MeemPath", ex);
			return null;
		}
		finally
		{
			GatewayManagerWedge.revokeTarget(proxy, client);
		}
	}

	public class MeemRegistryClientImpl implements MeemRegistryClient, ContentClient
	{
		public MeemRegistryClientImpl(PigeonHole pigeonHole)
		{
			this.pigeonHole = pigeonHole;
		}

		public void meemRegistered(Meem meem)
		{
			this.meem = meem;
		}

		public void meemDeregistered(Meem meem)
		{
			this.meem = null;
		}

		public void contentSent()
		{
			if (pigeonHole != null)
			{
				pigeonHole.put(meem);
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

		private PigeonHole pigeonHole;
		private Meem meem = null;
	}

	/**
	 * 
	 * MeemPathResolverClientImpl
	 * 
	 * @author stormboy
	 */
	public class MeemPathResolverClientImpl implements MeemResolverClient, ContentClient
	{
		public MeemPathResolverClientImpl(PigeonHole pigeonHole)
		{
			this.pigeonHole = pigeonHole;
		}

		public void meemResolved(MeemPath meemPath, Meem meem)
		{
			this.meem = meem;
		}

		public void contentSent()
		{
			if (pigeonHole != null)
			{
				pigeonHole.put(meem);
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

		private PigeonHole pigeonHole;
		private Meem meem = null;
	}

	private static final long timeout = 60000;

	/** Logger for the class */
	private static final Logger logger = LogFactory.getLogger();
}
