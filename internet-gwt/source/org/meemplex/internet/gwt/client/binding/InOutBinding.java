package org.meemplex.internet.gwt.client.binding;

import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetReference;

public abstract class InOutBinding {
	
	/**
	 * Inbound from the remote Meem
	 */
	private InboundBinding inboundBinding;

	/**
	 * Outbound to the remote Meem.
	 */
	private OutboundBinding outboundBinding;

	/**
	 * Factory for creating and destroying bindings.
	 */
	private BindingFactory bindingFactory;

	/**
	 * 
	 * @param bindingFactory
	 * @param inFacet Facet inbound to the remote Meem
	 * @param outFacet Facet outbound from the remote Meem
	 */
	protected InOutBinding(BindingFactory bindingFactory, FacetReference inFacet, FacetReference outFacet) {
		this.bindingFactory = bindingFactory;
		setOutboundBinding(bindingFactory.createOutboundBinding(inFacet));
		setInboundBinding(bindingFactory.createInboundBinding(outFacet));
	}

	private void setInboundBinding(InboundBinding inboundBinding) {
		this.inboundBinding = inboundBinding;
	}

	public InboundBinding getInboundBinding() {
		return inboundBinding;
	}

	private void setOutboundBinding(OutboundBinding outboundBinding) {
		this.outboundBinding = outboundBinding;
	}

	public OutboundBinding getOutboundBinding() {
		return outboundBinding;
	}

	public void release() {
		if (inboundBinding != null) {
			bindingFactory.releaseBinding(inboundBinding.getFacetReference());
		}
		if (outboundBinding != null) {
			bindingFactory.releaseBinding(outboundBinding.getFacetReference());
		}
	}
}
