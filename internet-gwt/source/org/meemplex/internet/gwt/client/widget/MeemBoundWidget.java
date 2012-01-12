package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.client.ClientFactory;
import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Widget that is Bound to a Facet or Pair of Facets.
 * 
 * @author stormboy
 *
 */
public abstract class MeemBoundWidget extends Composite {

	/**
	 * A label to use for the Button
	 */
	private String label = "Facet-Bound Widget";

	private BindingFactory bindingFactory;
	
	private String meemPath;
	
	/**
	 * Binding for receiving FactEvents from the outbound Facet of the server Meem
	 */
	private InboundBinding inboundBinding;
	
	/**
	 * Binding for sending FacetEvents to the inboundFacet of the server Meem.
	 */
	private OutboundBinding outboundBinding;

	public MeemBoundWidget() {
		this(null, null, null);
    }
	
	/**
	 * 
	 * @param bindingFactory
	 * @param name
	 * @param meemPath
	 */
	public MeemBoundWidget(String name, FacetReference inRef, FacetReference outRef) {
		this.bindingFactory = ClientFactory.spi.singleton().getFacetEventHub();
		
		//setElement(createElement());
		initWidget(createWidget());

		if (name != null) {
			setName(name);
		}
		
		if (inRef != null) {
			// an outbound binding on the input facet of the Meem
			setOutboundBinding(bindingFactory.createOutboundBinding(inRef));
		}
		if (outRef != null) {
			// inbound binding on the output facet of the Meem
			setInboundBinding(bindingFactory.createInboundBinding(outRef));
		}
    }
	
	/**
	 * This should return the Element that provides the UI for interacting with the Binary Faceyts of the Meem.
	 * 
	 * @return
	 */
	protected abstract Widget createWidget();
	
	public void setMeemPath(String meemPath) {
		unbind();	// clear existing bindings
		this.meemPath = meemPath;
	}
	
	public String getMeemPath() {
		return meemPath;
	}
	
	public void setName(String text) {
		this.label = text;
	}
	
	public String getName() {
	    return label;
    }
	
	protected void setInboundBinding(InboundBinding inboundBinding) {
	    this.inboundBinding = inboundBinding;
    }

	protected InboundBinding getInboundBinding() {
	    return inboundBinding;
    }

	protected void setOutboundBinding(OutboundBinding outboundBinding) {
	    this.outboundBinding = outboundBinding;
    }

	protected OutboundBinding getOutboundBinding() {
	    return outboundBinding;
    }
	
	@Override
	protected void onUnload() {
	    super.onUnload();
	    unbind();
	}

	/**
	 * relese bindings
	 */
	protected void unbind() {
		//GWT.log("unloading BinaryButton panel: " + meemPath + "|" + inboundBinding);
		if (inboundBinding != null) {
			bindingFactory.releaseBinding(inboundBinding.getFacetReference());
		}
		if (outboundBinding != null) {
			bindingFactory.releaseBinding(outboundBinding.getFacetReference());
		}
	}	

}
