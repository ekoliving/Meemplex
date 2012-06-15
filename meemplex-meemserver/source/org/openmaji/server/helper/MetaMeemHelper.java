/*
 * @(#)MetaMeemHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Create and use org.openmaji.meem.definition.MetaMeemAdapter.
 * - Complete acquiring information from all out-bound MeemMeta Facet methods.
 * - Consider adding Helper methods for in-bound MetaMeta Facet methods.
 * - Rewrite as a "HelperMeem", using the latest design pattern.
 */

package org.openmaji.server.helper;

import java.io.Serializable;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.FacetDefinition;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.meem.definition.MetaMeem;
import org.openmaji.system.meem.wedge.reference.ContentClient;




/**
 * <p>
 * MetaMeemHelper ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class MetaMeemHelper
{
	private static final long TIMEOUT = 60000L;

	public static FacetAttribute getFacetAttribute(Meem meem, String facetIdentifier)
	{
		PigeonHole<FacetAttribute> pigeonHole = new PigeonHole<FacetAttribute>();
		MetaMeem metaMeemClient = new MetaMeemClient(pigeonHole, facetIdentifier);
		Facet proxy = GatewayManagerWedge.getTargetFor(metaMeemClient, MetaMeem.class);

		Reference metaMeemClientReference = Reference.spi.create("metaMeemClient", proxy, true);

		try
		{
			meem.addOutboundReference(metaMeemClientReference, true);

			return pigeonHole.get(TIMEOUT);
		}
		catch (TimeoutException timeoutException)
		{
			// If no response, then return "null", as expected
			return null;
		}
		finally
		{
			GatewayManagerWedge.revokeTarget(proxy, metaMeemClient);
		}
	}
	
	public static MeemDefinition getMeemDefinition(Meem meem)
	{
		PigeonHole<MeemDefinition> pigeonHole = new PigeonHole<MeemDefinition>();
		MetaMeem metaMeemClient = new MeemDefinitionClient(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(metaMeemClient, MetaMeem.class);

		Reference metaMeemClientReference = Reference.spi.create("metaMeemClient", proxy, true);

		try
		{
			meem.addOutboundReference(metaMeemClientReference, true);

			return pigeonHole.get(TIMEOUT);
		}
		catch (TimeoutException timeoutException)
		{
			return null;
		}
		finally
		{
			GatewayManagerWedge.revokeTarget(proxy, metaMeemClient);
		}
	}

	/**
	 * 
	 */
	public static class MetaMeemClient implements MetaMeem, ContentClient
	{
		private PigeonHole<FacetAttribute> pigeonHole;
		private final String facetIdentifier;
		private FacetAttribute facetAttribute = null;

		public MetaMeemClient(PigeonHole<FacetAttribute> pigeonHole, String facetIdentifier)
		{
			this.pigeonHole = pigeonHole;
			this.facetIdentifier = facetIdentifier;
		}

		public void updateMeemAttribute(MeemAttribute meemAttribute)
		{
		}

		public void addWedgeAttribute(WedgeAttribute wedgeAttribute)
		{
		}

		public void updateWedgeAttribute(WedgeAttribute wedgeAttribute)
		{
		}

		public void removeWedgeAttribute(Serializable wedgeKey)
		{
		}

		public void addFacetAttribute(Serializable wedgeKey, FacetAttribute facetAttribute)
		{
			if (facetAttribute.getIdentifier().equals(facetIdentifier))
			{
				this.facetAttribute = facetAttribute;
			}
		}

		public void updateFacetAttribute(FacetAttribute facetAttribute)
		{
		}

		public void removeFacetAttribute(String facetKey)
		{
		}

		public void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute)
		{
		}

		public void updateDependencyAttribute(DependencyAttribute dependencyAttribute)
		{
		}

		public void removeDependencyAttribute(Serializable dependencyKey)
		{
		}

		public void contentSent()
		{
			if (pigeonHole != null)
			{
				pigeonHole.put(facetAttribute);
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
	
	/**
	 * 
	 */
	public static class MeemDefinitionClient implements MetaMeem, ContentClient
	{
		private PigeonHole<MeemDefinition> pigeonHole;
		private MeemDefinition meemDefinition = null;
		private WedgeDefinition lastWedgeDefinition = null;
		private FacetDefinition lastFacetDefinition = null;
		
		public MeemDefinitionClient(PigeonHole<MeemDefinition> pigeonHole)
		{
			this.pigeonHole = pigeonHole;
			meemDefinition = new MeemDefinition();
		}

		public void updateMeemAttribute(MeemAttribute meemAttribute)
		{
			meemDefinition.setMeemAttribute(meemAttribute);
		}

		public void addWedgeAttribute(WedgeAttribute wedgeAttribute)
		{
			lastWedgeDefinition = new WedgeDefinition(wedgeAttribute);
			meemDefinition.addWedgeDefinition(lastWedgeDefinition);
		}

		public void updateWedgeAttribute(WedgeAttribute wedgeAttribute)
		{
		}

		public void removeWedgeAttribute(Serializable wedgeKey)
		{
		}

		public void addFacetAttribute(Serializable wedgeKey, FacetAttribute facetAttribute)
		{
			lastFacetDefinition = new FacetDefinition(facetAttribute);
			lastWedgeDefinition.addFacetDefinition(lastFacetDefinition);
		}

		public void updateFacetAttribute(FacetAttribute facetAttribute)
		{
		}

		public void removeFacetAttribute(String facetKey)
		{
		}

		public void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute)
		{
			if (lastFacetDefinition == null) {
				//logger.log(Level.INFO, "cannot add DependencyAttribute. No 'lastFacetDefinition'");
				return;
			}
			
			// XXX
			//lastFacetDefinition.setDependencyAttribute(dependencyAttribute);
		}

		public void updateDependencyAttribute(DependencyAttribute dependencyAttribute)
		{
		}

		public void removeDependencyAttribute(Serializable dependencyKey)
		{
		}

		public void contentSent()
		{
			if (pigeonHole != null)
			{
				pigeonHole.put(meemDefinition);
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
