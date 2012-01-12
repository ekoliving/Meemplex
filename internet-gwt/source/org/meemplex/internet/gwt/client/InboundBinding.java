package org.meemplex.internet.gwt.client;

import java.util.HashSet;

import org.meemplex.internet.gwt.shared.Direction;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetReference;


/**
 * A binding on a remote Outbound Facet. This Binding *receives* FacetEvents from the
 * Facet, so it's called an InboundBinding.
 * 
 * TODO store a Filter reference in the Binding to enable distinction between Bindings with different Filters
 * 
 * @author Warren Bloomer
 *
 */
public class InboundBinding extends FacetBinding {

	/**
	 * Listeners for the facet events that occur for the facet.  
	 * Methods on the Facet interface of the listeners are invoked.
	 */
	private final HashSet<FacetEventListener> listeners = new HashSet<FacetEventListener>();	

	private FacetEventListener facetEventListener = new FacetEventListener() {
		public final void facetEvent(FacetEvent event) {
			// ignore events that do not relate to this binding
			if (isForThis(event)) {
				sendFacetEvent(event);
			}
		}
	};
	
	/**
	 * Constructor
	 * 
	 * @param for Facet Events
	 */
	public InboundBinding(FacetReference facetRef, FacetEventHub facetEventHub) {
		super(facetRef, facetEventHub);
		
		// The direction of the Facet is Outbound from the Meem (Inbound to this Binding)
		facetRef.setDirection(Direction.Outbound);
		
		facetEventHub.addFacetEventListener(facetEventListener);
	}

	/* --------------------- Methods that can be used by subclasses ---------------------- */

	/**
	 * Add a facet listener to the set of listeners.
	 */
	public final void addListener(FacetEventListener listener) {
		if (listener != null) {
			synchronized (listeners) {
				listeners.add(listener);
			}
		}
	}

	/**
	 * Remove a facet listener from the set of listeners.
	 */
	public final void removeListener(FacetEventListener listener) {
		if (listener != null) {
			synchronized (listeners) {
				listeners.remove(listener);
			}		
		}
	}

	/**
	 * Stop listening on the EventHub, and remove listeners from this Binding.
	 */
	public void release() {
		super.release();
		getEventHub().removeFacetEventListener(facetEventListener);
		synchronized (listeners) {			
			listeners.clear();
		}
	}

	/**
	 * This is called before a FacetEvent is translated to a method invocation 
	 * on the listeners.
	 * 
	 * This may be overridden if, for example, the parameters passed over RPC
	 * transport do not match particular params of the method on the Facet.
	 * 
	 * @param methodName the name of the method
	 * @param params     the original parameters from the FacetEvent
	 * @return           the normalize parameters
	 */
	protected Object[] normalize(String methodName, Object[] params) {
		return params;
	}

	
	/* ------------------------- utility methods ---------------------------- */
	
	/**
	 * Whether the Event is for this Binding.
	 * 
	 * This Binding is attached to the remote Meem, so we check the "from" address.
	 * 
	 * @param event
	 */
	private boolean isForThis(FacetEvent event) {
		
		FacetReference from = event.getFrom();
		if (from == null) {
			return false;
		}
		
		String meemPath = from.getMeemPath();
		if (meemPath == null) {
			return false;
		}
		
		String facetId = from.getFacetId();
		if (facetId == null) {
			return false;
		}
		
		return  (
				meemPath.equalsIgnoreCase(getMeemPath()) && 
				facetId.equalsIgnoreCase(getFacetId())
			);
	}
	
	/**
	 * Send the FacetEvent to listeners of this Binding.
	 * 
	 * @param event
	 */
	private void sendFacetEvent(FacetEvent event) {
		synchronized (listeners) {
			for (FacetEventListener listener : listeners) {
				listener.facetEvent(event);
			}
		}
	}

}
