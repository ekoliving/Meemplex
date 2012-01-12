package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.client.ClientFactory;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONParser;

/**
 * A button to interface with binary facets of a meem.
 * 
 * TODO get icon(s) from meem.
 * 
 * @author stormboy
 *
 */
public abstract class BinaryWidget extends MeemBoundWidget {

	private static boolean debug = false;

	/**
	 * The name of the Facet inbound to the remote Meem on the server
	 */
	private static final String inboundFacet = "binaryInput";

	/**
	 * The name of the Facet outbound from the remote Meem on the server.
	 */
	private static final String outboundFacet = "binaryOutput";

	/**
	 * 
	 */
	private String trueText = "on";

	/**
	 * 
	 */
	private String falseText = "off";
	
	public BinaryWidget() {
    }
	
	/**
	 * Constructor
	 * 
	 * @param bindingFactory
	 * @param name
	 * @param meemPath
	 */
	public BinaryWidget(String name, String meemPath) {
		this(name, meemPath, inboundFacet, outboundFacet);
    }
	
	public BinaryWidget(String name, String meemPath, String inboundFacet, String outboundFacet) {
		super(
				name,
				new FacetReference(meemPath, inboundFacet, FacetClasses.BINARY),
				new FacetReference(meemPath, outboundFacet, FacetClasses.BINARY)
			);
		
		addBindingListeners();
	}
	
	@Override
	public void setMeemPath(String meemPath) {
	    super.setMeemPath(meemPath);

	    BindingFactory bindingFactory = ClientFactory.spi.singleton().getFacetEventHub();
		FacetReference inRef = new FacetReference(meemPath, inboundFacet, FacetClasses.BINARY);
		FacetReference outRef = new FacetReference(meemPath, outboundFacet, FacetClasses.BINARY);

		setOutboundBinding(bindingFactory.createOutboundBinding(inRef));
		setInboundBinding(bindingFactory.createInboundBinding(outRef));
		addBindingListeners();
	}



	/**
	 * Handle a value from the InboundBinding.
	 * 
	 * @param value
	 */
	protected abstract void handleValue(Boolean value);

	/**
	 * Handle a HealthEvent from the Inbound or Outbound Binding
	 * 
	 * @param event
	 */
	protected abstract void handleHealth(FacetHealthEvent event);


	public void setOnText(String trueText) {
	    this.trueText = trueText;
    }

	public String getOnText() {
	    return trueText;
    }

	public void setOffText(String falseText) {
	    this.falseText = falseText;
    }

	public String getOffText() {
	    return falseText;
    }
	
	
	private void addBindingListeners() {
		getOutboundBinding().addBindingHealthListener(new FacetHealthListener() {
			public void facetHealthEvent(FacetHealthEvent event) {
				if (debug) {
					GWT.log("BinaryWidget: got health event: " + event);
				}
				handleHealth(event);
			}
		});
				
		getInboundBinding().addBindingHealthListener(new FacetHealthListener() {
			public void facetHealthEvent(FacetHealthEvent event) {
				if (debug) {
					GWT.log("BinaryWidget: got health event: " + event);
				}
				handleHealth(event);
			}
		});

		getInboundBinding().addListener(new FacetEventListener() {
			public void facetEvent(FacetEvent event) {
				if (debug) {
					GWT.log("BinaryWidget: got facet event: " + event);
				}
				if ("valueChanged".equals(event.getMethod())) {
					JSONBoolean val = JSONParser.parseLenient(event.getParams()[0]).isBoolean();
					if (val != null) {
						handleValue(val.booleanValue());
					}
				}
			}
		});
	}

}
