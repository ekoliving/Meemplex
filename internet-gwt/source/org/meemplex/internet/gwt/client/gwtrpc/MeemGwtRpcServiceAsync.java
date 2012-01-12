package org.meemplex.internet.gwt.client.gwtrpc;

import java.util.ArrayList;
import java.util.List;

import org.meemplex.internet.gwt.shared.ErrorEvent;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetReference;
import org.meemplex.internet.gwt.shared.KnockOutEvent;
import org.meemplex.internet.gwt.shared.MeemEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MeemGwtRpcServiceAsync {

	/**
	 * Register interest in a Facet's health, and in the case of an outbound Facet (from the
	 * point of view of the Meem) also interested in FacetEvents.
	 * 
	 * @param facetReference
	 * @param callback
	 */
	void registerDependency(FacetReference facetReference, AsyncCallback<Void> callback);
	
	/**
	 * De-register interest in a Facet's health
	 */
	void removeDependency(FacetReference facetReference, AsyncCallback<Void> callback);
	
	/**
	 * Send a message to a Facet
	 * 
	 */
	void send(MeemEvent[] events, AsyncCallback<Void> callback);

	/**
	 * Receive all the events in the queue outbound from Maji.
	 * 
	 * This method will block until either events are available, or until a timeout
	 * occurs.
	 * 
	 * The types of events that may be retuen are
	 *   - FacetHealth events that give a status of Meem or Facet that has been requested
	 *   - Facet events with messages to be sent from Facets on the server to the client.
	 *   - KnockOut event when another receive() request has been made in the same session
	 *     so this request has been returned to avoid overlap issues.
	 * 
	 * @param callback provides a List of MeemEvents.
	 */
	void receive(AsyncCallback<MeemEvent[]> callback);

	/**
	 * Interrupt a blocking receive call.
	 * 
	 */
	void interrupt(AsyncCallback<Void> callback);

	/**
	 * Terminate the session
	 * 
	 */
	void bye(AsyncCallback<Void> callback);
	
	/**
	 * Hack to force GWT to compile classes.
	 */
	void dummy(MeemEvent me, FacetEvent fe, FacetHealthEvent fhe, ErrorEvent ee, KnockOutEvent koe, ArrayList<MeemEvent> list, AsyncCallback<Void> callback );

}
