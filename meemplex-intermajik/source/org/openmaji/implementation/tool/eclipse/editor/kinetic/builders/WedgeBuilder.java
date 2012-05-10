/*
 * @(#)WedgeBuilder.java
 * Created on 19/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.MetaMeemStub;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.FacetInbound;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.FacetOutbound;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.FacetInboundAttribute;
import org.openmaji.meem.definition.FacetOutboundAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MetaMeem;



/**
 * <code>WedgeBuilder</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeBuilder extends ElementContainerBuilder {
	//=== Internal MetaMeemClient Implementation =================================
	private MetaMeem metaMeemClient = new MetaMeemStub() {
		public void addFacetAttribute(Serializable wedgeKey, FacetAttribute facetAttribute) {
			if(!wedgeKey.equals(getModel().getId())) return;
			WedgeBuilder.this.addFacetAttribute(facetAttribute);
		}
		public void updateFacetAttribute(FacetAttribute facetAttribute) {
			WedgeBuilder.this.updateFacetAttribute(facetAttribute);
		}
		public void removeFacetAttribute(Serializable facetKey) {
			WedgeBuilder.this.removeFacetAttribute(facetKey);
		}
	};
	
	public WedgeBuilder(Wedge wedge) {
		super(wedge);
	}
	
	protected Wedge getWedge() {
		return (Wedge)getModel();
	}
	
	public MeemStructure getStructure() {
		return getWedge().getMeem().getProxy().getMetaMeem().getStructure();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.model.builders.ElementBuilder#activate()
	 */
	public void activate() {
		getWedge().getMeem().getProxy().getMetaMeem().addClient(metaMeemClient);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.model.builders.ElementBuilder#deactivate()
	 */
	public void deactivate() {
		getWedge().getMeem().getProxy().getMetaMeem().removeClient(metaMeemClient);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#buildContents()
	 */
	protected void refreshContents() {
		MeemStructure structure = getStructure();
		WedgeAttribute wedgeAttribute = structure.getWedgeAttribute(getModel().getId());
		
		// Build a map that maps facet attribute id to view facet model.
		Map tempFacets = createChildElementMap();
		
		Iterator<String> itFacetAttributeKey = structure.getFacetAttributeKeys(wedgeAttribute.getIdentifier()).iterator();
		while(itFacetAttributeKey.hasNext()) {
			String facetAttributeKey = itFacetAttributeKey.next();
			FacetAttribute facetAttribute = structure.getFacetAttribute(facetAttributeKey);
			Facet facet = (Facet)tempFacets.get(facetAttributeKey);
			if(facet == null) {
				// Facet is NOT found in the wedge, create a new one and append it
				addFacetAttribute(facetAttribute);
			}
			else {
				//getChildBuilder(wedge.getId()).build();
				tempFacets.remove(facetAttributeKey);
			}
		}

		// Removing all orphan facets remaining in the map
		Iterator itFacet = tempFacets.values().iterator();
		while(itFacet.hasNext()) {
			Facet facet = (Facet)itFacet.next();
			getContainerModel().removeChild(facet);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearContents()
	 */
	protected void clearContents() {
		// Going through all wedges in the view model		
		Element[] children = createChildElementArray();
		for(int i = 0; i < children.length; i++) {
			Facet facet = (Facet)children[i];
			removeFacetAttribute(facet.getAttributeIdentifier());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#refreshConnections()
	 */
	protected void refreshConnections() {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearConnections()
	 */
	protected void clearConnections() {
	}

	protected void addFacetAttribute(FacetAttribute facetAttribute) {
		Facet facet = (Facet)getWedge().findElement(facetAttribute.getIdentifier());
	
		if(facet == null) {
			// No facet of the facet id exists, create the view model
			MeemClientProxy proxy = getWedge().getMeem().getProxy();
			if(facetAttribute.isDirection(Direction.INBOUND)) {
				// Create new inbound facet
				facet = new FacetInbound(proxy, (FacetInboundAttribute)facetAttribute);
			}
			else
			if(facetAttribute.isDirection(Direction.OUTBOUND)) {
				// Create new outbound facet
				facet = new FacetOutbound(proxy, (FacetOutboundAttribute)facetAttribute);
			}
			getContainerModel().addChild(facet);
		}

		ElementBuilder builder = getChildBuilder(facet.getId());
		if(builder == null) {
			addChildBuilder(new FacetBuilder(facet));
		}
		if(!loadVariable(facet)) addVariable(facet);
	}
	
	protected void updateFacetAttribute(FacetAttribute facetAttribute) {
		Element element = getContainerModel().findElement(facetAttribute.getIdentifier());
		if(element == null) return;
		Facet facet = (Facet)element;
		facet.updateAttribute(facetAttribute);
	}
	
	protected void removeFacetAttribute(Object facetKey) {
		Element facet= getWedge().findElement(facetKey);
		if(facet != null) {
			removeChild(facet.getId()); 
		} 
	}
}
