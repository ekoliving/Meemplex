/*
 * Created on 9/09/2004
 *
 */
package org.openmaji.rpc.binding;

import org.openmaji.meem.Facet;

/**
 * A Service Provider Interface (SPI) for retrieving bindings for inbound and outbound Facets
 * 
 * @author Warren Bloomer
 *
 */
public interface BindingSPI {
	
	/**
	 * Return an OutboundBinding for the facet class
	 * 
	 * @param facetClass
	 */
	public OutboundBinding getOutboundBinding(Class<? extends Facet> facetClass);

	/**
	 * Return an InboundBinding for the facet class
	 * 
	 * @param facetClass
	 */
	public InboundBinding getInboundBinding(Class<? extends Facet> facetClass);

	/**
	 * Register an outbound binding implementation for a given
	 * facet interface.
	 * 
	 * @param facetClass The interface Class for the facet.
	 * @param bindingClass The class that handles the binding.
	 */
	public void registerOutbound(Class<? extends Facet> facetClass, Class<? extends OutboundBinding> bindingClass);

	/**
	 * Register an inboudn binding implementation for a given
	 * facet type.
	 * 
	 * @param facetClass The interface for the Facet.
	 * @param bindingClass the class that handles the binding.
	 */
	public void registerInbound(Class<? extends Facet> facetClass, Class<? extends InboundBinding> bindingClass);

}
