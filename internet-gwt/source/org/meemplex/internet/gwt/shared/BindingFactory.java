package org.meemplex.internet.gwt.shared;

import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.OutboundBinding;

/**
 * The Factory creates and destroys FacetBindings
 * 
 * @author stormboy
 *
 */
public interface BindingFactory {
	
	InboundBinding createInboundBinding(FacetReference facetReference);

	OutboundBinding createOutboundBinding(FacetReference facetReference);
	
	void releaseBinding(FacetReference facetReference);
}
