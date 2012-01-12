package org.meemplex.internet.gwt.client;

import java.util.HashSet;

import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetReference;


/**
 * A Binding to a Facet on a (remote) Meem.
 * 
 * @author Warren Bloomer
 *
 */
public class FacetBinding {
	
	private FacetReference facetReference;
	
	private final HashSet<FacetHealthListener> healthListeners = new HashSet<FacetHealthListener>();
	
	private FacetHealthEvent lastHealthEvent; 

	private FacetEventHub eventHub;
	
	private FacetHealthListener facetHealthListener = new FacetHealthListener() {
		public final void facetHealthEvent(FacetHealthEvent event) {
			// ignore events that do not relate to this binding
			if (isForThis(event)) {
				sendHealth(event);
			}
		}
	};
	
	/**
	 * Constructor
	 * 
	 * @param eventHub
	 */
	FacetBinding(FacetReference facetRef, FacetEventHub eventHub) {
		setFacetReference(facetRef);
		this.eventHub = eventHub;

		// listen for health events for this Binding on the FacetEvenHub
		eventHub.addFacetHealthListener(facetHealthListener);
    }

	private void setFacetReference(FacetReference facetReference) {
	    this.facetReference = facetReference;
    }

	public FacetReference getFacetReference() {
	    return facetReference;
    }

	/**
	 * A URI representation of a MeemPath
	 * 
	 * examples of valid locations:
	 * 		hyperspace:/cat1/cat2/MyMeem
	 * 		meemstore:/uuid
	 * 		transient:/uuid
	 * 
	 * @param location
	 */
	public String getMeemPath() {
		return facetReference == null ? null : facetReference.getMeemPath();
	}

	/**
	 * Returns the Facet identifier.
	 */	
	public String getFacetId() {
		return facetReference == null ? null : facetReference.getFacetId();
	}
	
	/**
	 * Returns the Facet class for this Binding.
	 */
	public String getFacetClass() {
		return facetReference == null ? null : facetReference.getFacetClass();
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void addBindingHealthListener(FacetHealthListener listener) {
		synchronized (healthListeners) {
			healthListeners.add(listener);
		}
		
		// send last health event
		if (lastHealthEvent != null) {
			listener.facetHealthEvent(lastHealthEvent);
		}
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeBindingHealthListener(FacetHealthListener listener) {
		synchronized (healthListeners) {			
			healthListeners.remove(listener);
		}
	}

	protected FacetEventHub getEventHub() {
		return eventHub;
	}
	
	/**
	 * Free this binding.
	 */
	public void release() {
		eventHub.removeFacetHealthListener(facetHealthListener);
		synchronized (healthListeners) {			
			healthListeners.clear();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isBound() {
		boolean result = false;
		if (lastHealthEvent != null) {
			result = lastHealthEvent.getBindingState() == FacetHealthEvent.FACET_RESOLVED;
		}
		return result;
	}
	
	public String getLifeCycleState() {
		String result = null;
		if (lastHealthEvent != null) {
			result = lastHealthEvent.getLifeCycleState(); 
		}
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public FacetHealthEvent getFacetHealth() {
		return lastHealthEvent;
	}
	
	/**
	 * 
	 */
	public boolean equals(Object o) {
		if (o instanceof FacetBinding) {
			FacetBinding fb = (FacetBinding)o;
			
			return facetReference == null ? facetReference == fb.facetReference : facetReference.sameFacetPath(fb.facetReference);
		}
		return false;
	}

	/**
	 * 
	 */
	public int hashCode() {
		int hash = 20573988;
		hash ^= facetReference == null ? 0 : facetReference.hashCode();
		
		return hash;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass());
		sb.append("[Binding: ");
		if (facetReference != null) {
			sb.append(facetReference.getMeemPath());
			sb.append(" . ");
			sb.append(facetReference.getFacetId());
			sb.append(" : ");
			sb.append(facetReference.getFacetClass());
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	
	/* ------------------------- utility methods ---------------------------- */
	
	/**
	 * 
	 * @param event
	 */
	private boolean isForThis(FacetHealthEvent event) {
		return
			facetReference != null &&
			event != null &&
			( event.getMeemPath() == null || event.getMeemPath().equalsIgnoreCase(facetReference.getMeemPath()) ) &&
			( event.getFacetId() == null || event.getFacetId().equalsIgnoreCase(facetReference.getFacetId()) );
	}

	
	/**
	 *
	 * @param event
	 */
	protected void sendHealth(FacetHealthEvent event) {
		synchronized (healthListeners) {
			for (FacetHealthListener listener : healthListeners) {
				listener.facetHealthEvent(event);
			}
		}
	}

}
