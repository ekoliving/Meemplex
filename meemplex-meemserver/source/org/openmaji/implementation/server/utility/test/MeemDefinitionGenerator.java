/*
 * @(#)MeemDefinitionGenerator.java
 * Created on 20/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.utility.test;


import org.openmaji.meem.definition.*;

/**
 * <code>MeemDefinitionGenerator</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemDefinitionGenerator {
	static private int WEDGE_MIN = 1;
	static private int WEDGE_MAX = 5;
	static private int FACET_MIN = 2;
	static private int FACET_MAX = 5;
	
	static private int nextId = 0;
	
	/**
	 * Creates a random meem.
	 * @return MeemDefinition
	 */
	static public MeemDefinition createRandom() {
		MeemDefinition meemDefinition = createEmpty();
		createWedges(meemDefinition);
		return meemDefinition; 
	}
	
	static public MeemDefinition createEmpty() {
		MeemDefinition meemDefinition = new MeemDefinition();
		meemDefinition.getMeemAttribute().setScope(Scope.LOCAL);
		meemDefinition.getMeemAttribute().setIdentifier("Randomly Constructed Meem " + Integer.toString(++nextId));
		meemDefinition.getMeemAttribute().setVersion(1);
		return meemDefinition; 
	}
	
	static protected void createWedges(MeemDefinition meemDefinition) {
		double range = WEDGE_MAX - WEDGE_MIN;
		int count = (int)(Math.random() * range) + WEDGE_MIN;
		for(int wedge = 0; wedge < count; wedge++) {
			WedgeDefinition wedgeDefinition = new WedgeDefinition();
			wedgeDefinition.getWedgeAttribute().setImplementationClassName(MeemDefinitionGenerator.class.toString()+Integer.toString(wedge));
			createFacets(wedgeDefinition);
			meemDefinition.addWedgeDefinition(wedgeDefinition);
		}
	}
	
	static protected void createFacets(WedgeDefinition wedgeDefinition) {
		double range = FACET_MAX - FACET_MIN;
		int count = (int)(Math.random() * range) + FACET_MIN;
		for(int facet = 0; facet < count; facet++) {
			boolean inbound = (Math.random() > 0.5);
			String facetIdentifier = "facet " + Integer.toString(facet);
			String name = (inbound)? "Inbound ":"Outbound Interface " + Integer.toString(facet);
			FacetAttribute facetAttr = null;
			if(inbound) {
				facetAttr = new FacetInboundAttribute(facetIdentifier, name);
			}
			else {
				facetAttr = new FacetOutboundAttribute(name, facetIdentifier);
			}
			FacetDefinition facetDefinition = new FacetDefinition(facetAttr);
			wedgeDefinition.addFacetDefinition(facetDefinition);
		}
		
	}
}
