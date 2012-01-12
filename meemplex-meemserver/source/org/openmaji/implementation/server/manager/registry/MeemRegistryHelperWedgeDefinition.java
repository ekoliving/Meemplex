/*
 * @(#)MeemRegistryHelperWedgeDefinition.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry;


import org.openmaji.meem.definition.*;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemRegistryHelperWedgeDefinition {
	private static WedgeDefinition wedgeDefinition = null;
	
	public static WedgeDefinition getWedgeDefinition() {
		
		if (wedgeDefinition == null) {
			
			WedgeAttribute wedgeAttribute;
			FacetInboundAttribute facetInboundAttribute;
			FacetOutboundAttribute facetOutboundAttribute;
			FacetDefinition facetDefinition;

			wedgeAttribute = new WedgeAttribute();
			wedgeAttribute.setImplementationClassName("org.openmaji.server.manager.registry.MeemRegistryGatewayWedge");

			wedgeDefinition = new WedgeDefinition(wedgeAttribute);

			// inbound facets

			facetInboundAttribute = new FacetInboundAttribute("meemRegistryHelperClient",
				"org.openmaji.system.manager.registry.MeemRegistryClient", false);

			facetDefinition = new FacetDefinition(facetInboundAttribute);
			wedgeDefinition.addFacetDefinition(facetDefinition);

			// outbound facets

			facetOutboundAttribute = new FacetOutboundAttribute(
				"org.openmaji.meem.Meem", "meemFacet");
			facetDefinition = new FacetDefinition(facetOutboundAttribute);
			wedgeDefinition.addFacetDefinition(facetDefinition);

			facetOutboundAttribute = new FacetOutboundAttribute(
				"org.openmaji.system.manager.registry.MeemRegistry", "meemRegistryHelperFacet");
			facetDefinition = new FacetDefinition(facetOutboundAttribute);
			wedgeDefinition.addFacetDefinition(facetDefinition);
		}
		
		return wedgeDefinition;
	}
}
