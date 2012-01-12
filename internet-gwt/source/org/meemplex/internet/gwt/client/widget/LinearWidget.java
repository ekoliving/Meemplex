package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.client.facets.Linear;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Panel;

/**
 * A button to interface with Linear facets of a meem.
 * 
 * TODO get icon(s) from meem.
 * 
 * @author stormboy
 *
 */
public abstract class LinearWidget extends MeemBoundWidget {

	private static boolean debug = false;

	/**
	 * The name of the Facet inbound to the remote Meem on the server
	 */
	private static final String inboundFacet = "linearInput";

	/**
	 * The name of the Facet outbound from the remote Meem on the server.
	 */
	private static final String outboundFacet = "linearOutput";

	/**
	 * The unit for the value
	 */
	private String unit = "%";
	
	/**
	 * The change in value to be made when incrementing or decrementing the Linear
	 * value in discrete steps.
	 */
	private Number delta = 10;

	/**
	 * 
	 */
	private Number min = 0;
	
	/**
	 * 
	 */
	private Number max = 100;
	
	/**
	 * Constructor
	 * 
	 * @param bindingFactory
	 * @param name
	 * @param meemPath
	 */
	public LinearWidget(String name, String meemPath) {
		super(
				name,
				new FacetReference(meemPath, inboundFacet, FacetClasses.LINEAR),
				new FacetReference(meemPath, outboundFacet, FacetClasses.LINEAR)
			);

		initWidget(getPanel());
		
		getOutboundBinding().addBindingHealthListener(new FacetHealthListener() {
			public void facetHealthEvent(FacetHealthEvent event) {
				if (debug) {
					GWT.log("LinearWidget: got health event: " + event);
				}
				handleHealth(event);
			}
		});
				
		getInboundBinding().addBindingHealthListener(new FacetHealthListener() {
			public void facetHealthEvent(FacetHealthEvent event) {
				if (debug) {
					GWT.log("LinearWidget: got health event: " + event);
				}
				handleHealth(event);
			}
		});
		
		getInboundBinding().addListener(new FacetEventListener() {
			public void facetEvent(FacetEvent event) {
				if (debug) {
					GWT.log("LinearWidget: got facet event: " + event);
				}
				if ("valueChanged".equals(event.getMethod())) {
					JSONValue val = JSONParser.parseLenient(event.getParams()[0]);
					handleValue(val.isNumber().doubleValue());
				}
			}
		});
    }
	

	public void setUnit(String unit) {
	    this.unit = unit;
    }


	public String getUnit() {
	    return unit;
    }


	public void setDelta(Number delta) {
	    this.delta = delta;
    }


	public Number getDelta() {
	    return delta;
    }


	public void setMin(Number min) {
	    this.min = min;
    }


	public Number getMin() {
	    return min;
    }


	public void setMax(Number max) {
	    this.max = max;
    }


	public Number getMax() {
	    return max;
    }

	/**
	 * Handle a value from the InboundBinding.
	 * 
	 * @param value
	 */
	protected abstract void handleValue(double value);

	/**
	 * Handle a HealthEvent from the Inbound or Outbound Binding
	 * 
	 * @param event
	 */
	protected abstract void handleHealth(FacetHealthEvent event);

	/**
	 * This should return the Panel that provides the UI for interacting with the Binary Faceyts of the Meem.
	 * 
	 * @return
	 */
	protected abstract Panel getPanel();

}
