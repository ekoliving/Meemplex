package org.meemplex.internet.gwt.client;

import org.meemplex.internet.gwt.shared.Direction;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.json.client.JSONValue;

/**
 * @author Warren Bloomer
 */
public class OutboundBinding extends FacetBinding /*implements ContentClient*/ {
	
	private FacetEventListener facetEventListener;
	
	/**
	 * Constructor
	 */
	public OutboundBinding(FacetReference facetRef, FacetEventHub hub) {
		super(facetRef, hub);
		
		// direction of Facet is Inbound to the Meem.
		facetRef.setDirection(Direction.Inbound);
		
		this.facetEventListener = hub;
	}
	
	/**
	 * Send a message to the Facet bound by this binding.
	 * 
	 * @param methodName
	 * @param args
	 */
	public final void send(String method, JSONValue[] params) {
		FacetReference from = null;					// this is a "proxy", so no meem/facet reference
		FacetReference to = getFacetReference();	// the facet to send the message to.
		
		String[] stringParams = null;
		if (params != null) {
			stringParams = new String[params.length];
			int i=0;
			for (JSONValue s : params) {
				stringParams[i++] = s.toString();
			}
		}
		
		FacetEvent event = new FacetEvent(from, to, method, stringParams);

		send(event);
	}

	private void send(FacetEvent event) {
		if (facetEventListener != null) {
			facetEventListener.facetEvent(event);
		}
	}
	
	/*
	public void contentSent() {
		FacetHealthEvent event = new FacetHealthEvent(FacetHealthEvent.FACET_RESOLVED, null);
		event.setMeemPath(getMeemPath());
		event.setFacetId(getFacetId());
		event.setFacetClass(getFacetClass());
		sendHealth(event);
	}
	
	public void contentFailed(String reason) {
		FacetHealthEvent event = new FacetHealthEvent(FacetHealthEvent.FACET_NOTRESOLVED, null);
		event.setMeemPath(getMeemPath());
		event.setFacetId(getFacetId());
		event.setFacetClass(getFacetClass());
		sendHealth(event);
	}
	*/
}
