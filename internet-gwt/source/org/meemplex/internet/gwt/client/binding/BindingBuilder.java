package org.meemplex.internet.gwt.client.binding;

import org.meemplex.internet.gwt.client.FacetEventHub;
import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.client.binding.impl.CategoryInboundBinding;
import org.meemplex.internet.gwt.client.binding.impl.CategoryOutputBinding;
import org.meemplex.internet.gwt.client.binding.impl.PropertiesOutboundBinding;
import org.meemplex.internet.gwt.client.binding.impl.PropertiesSpecInboundBinding;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;

public class BindingBuilder {

	public static InboundBinding createInbound(FacetReference facetReference, FacetEventHub eventHub) {
		InboundBinding binding;
		
		//GWT.log("creating in binding for: " + facetReference.getMeemPath() + " / " + facetReference.getFacetId() + " : " + facetReference.getFacetClass());
		
		if (FacetClasses.BINARY.equals(facetReference.getFacetClass())) {
			binding = new InboundBinding (facetReference, eventHub);
		}
		else if (FacetClasses.LINEAR.equals(facetReference.getFacetClass())) {
			binding = new InboundBinding (facetReference, eventHub);
		}
//		else if (FacetClasses.CATEGORY.equals(facetReference.getFacetClass())) {
//		}
		else if (FacetClasses.CATEGORY_CLIENT.equals(facetReference.getFacetClass())) {
			binding = new CategoryInboundBinding(facetReference, eventHub);
		}
		else if (FacetClasses.FACET_CLIENT.equals(facetReference.getFacetClass())) {
			binding = new InboundBinding (facetReference, eventHub);
		}
		else if (FacetClasses.CONFIG_CLIENT.equals(facetReference.getFacetClass())) {
			//GWT.log("got config client: " + FacetClasses.CONFIG_CLIENT);
			binding = new PropertiesSpecInboundBinding(facetReference, eventHub);
		}
		else {
			//GWT.log("default inbound biding: " + facetReference);
			binding = new InboundBinding (facetReference, eventHub);
		}
		
		return binding;
	}
	
	public static OutboundBinding createOutbound(FacetReference facetReference, FacetEventHub eventHub) {
		OutboundBinding binding;
		
		if (FacetClasses.BINARY.equals(facetReference.getFacetClass())) {
			binding = new OutboundBinding (facetReference, eventHub);
		}
		else if (FacetClasses.LINEAR.equals(facetReference.getFacetClass())) {
			binding = new OutboundBinding (facetReference, eventHub);
		}
		else if (FacetClasses.CATEGORY.equals(facetReference.getFacetClass())) {
			binding = new CategoryOutputBinding(facetReference, eventHub);
		}
//		else if (FacetClasses.CATEGORY_CLIENT.equals(facetReference.getFacetClass()) {
//			binding = new CategoryInboundBinding(facetReference, eventHub);
//		}
		else if (FacetClasses.FACET_CLIENT.equals(facetReference.getFacetClass())) {
			binding = new OutboundBinding (facetReference, eventHub);
		}
		else if (FacetClasses.CONFIG_HANDLER.equals(facetReference.getFacetClass())) {
			GWT.log("got config handler: " + FacetClasses.CONFIG_HANDLER);
			binding = new PropertiesOutboundBinding(facetReference, eventHub);
		}
		else {
			binding = new OutboundBinding (facetReference, eventHub);
		}
		
		return binding;
	}
}
