/*
 * Created on 9/09/2004
 *
 */
package org.openmaji.rpc.binding;

import org.openmaji.implementation.rpc.binding.BindingSPIImpl;
import org.openmaji.meem.Facet;


/**
 * A Factory for creating inbound and outbound Maji RPC Bindings.
 * 
 * These bindings are required to map Facet interfaces into RPC specific
 * calls.
 * 
 * @author Warren Bloomer
 *
 */
public class BindingFactory {

	private static BindingSPI spi = new BindingSPIImpl();

	/**
	 * Retrieve an outbound binding for the given facet class
	 * 
	 * @param facetClass
	 * @return OutboundBinding or null if none are available for the facet class
	 */
	public static OutboundBinding getOutboundBinding(Class<? extends Facet> facetClass) {
		return spi.getOutboundBinding(facetClass);
	}

	/**
	 * Retrieve an inbound binding for the given facet class
	 * 
	 * @param facetClass
	 * @return InboundBinding or null if none are available for the facet class
	 */
	public static InboundBinding getInboundBinding(Class<? extends Facet> facetClass) {
		return spi.getInboundBinding(facetClass);
	}

	/**
	 * Register a new SPI
	 * 
	 * @param spi
	 */
	public static void register(BindingSPI spi) {
		BindingFactory.spi = spi;
	}

	/**
	 * Return the service provider for this binding factory.
	 *   
	 * @return the service provider.
	 */
	public static BindingSPI getBindingSPI() {
		return spi;
	}
}
