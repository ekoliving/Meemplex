package org.meemplex.internet.gwt.client.gwtrpc;

import java.util.ArrayList;
import java.util.List;

import org.meemplex.internet.gwt.shared.ErrorEvent;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetReference;
import org.meemplex.internet.gwt.shared.KnockOutEvent;
import org.meemplex.internet.gwt.shared.MeemEvent;
import org.meemplex.internet.gwt.shared.NewSessionException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A remote service for sending and receiving FacetEvents with a Meemplex server.
 * 
 * @author stormboy
 *
 */
@RemoteServiceRelativePath("../meemplex_gwt/rpc")
public interface MeemGwtRpcService extends RemoteService {
	
	/**
	 * Register interest in a Facet inbound to or outbound from the server.
	 * 
	 * @param facetReference
	 * @return
	 */
	void registerDependency(FacetReference facetReference)
		throws NewSessionException;
	
	/**
	 * De-register interest in a Facet
	 */
	void removeDependency(FacetReference facetReference)
		throws NewSessionException;
	
	/**
	 * Send meem events. Usually a message to a Facet
	 * 
	 */
	void send(MeemEvent[] events)
		throws NewSessionException;
	
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
	 * @return a Listr of MeemEvents.
	 */
	MeemEvent[] receive() 
		throws NewSessionException;

	/**
	 * Interrupt a blocking receive call.
	 * 
	 */
	void interrupt()
		throws NewSessionException;

	/**
	 * Terminate the session
	 * 
	 */
	void bye();

	/**
	 * A hack to make sure these Objects are compiled by the GWT toolkit
	 */
	void dummy(MeemEvent me, FacetEvent fe, FacetHealthEvent fhe, ErrorEvent ee, KnockOutEvent koe, ArrayList<MeemEvent> list);
}