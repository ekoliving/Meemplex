/*
 * Created on 23/08/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openmaji.rpc.binding;

import java.util.Collection;
import java.util.HashSet;

import org.openmaji.meem.Facet;
import org.openmaji.system.meem.wedge.reference.ContentClient;



/**
 *
 * Note:-
 * The subclass of this has to be added to the file META-INF/registryFile.mrpc 
 * to register the binding.
 * 
 * * @author Warren Bloomer
 */
public class OutboundBinding extends FacetBinding implements Facet, ContentClient {
	//private static final Logger logger = Logger.getAnonymousLogger();
	
	private Collection<FacetEventListener> facetEventListeners = new HashSet<FacetEventListener>();
	
	/**
	 *
	 */
	public OutboundBinding() {
	}

	/**
	 * Set the listener for FacetEvents
	 * 
	 * @param listener
	 */
	public final void addFacetEventListener(FacetEventListener listener) {
		synchronized(facetEventListeners) {
			this.facetEventListeners.add(listener);
	
			// TODO perhaps register the binding, so that Facet References may be resolved pre-invocation
	
			// register to listen to FacetHealth events
			if (listener instanceof FacetHealthSender) {
				//System.out.println("adding this, " + getMeemPath() + ", as a health listener");
				((FacetHealthSender)listener).addFacetHealthListener(this);
			}
		}
	}
	
	public final synchronized void removeFacetEventListener(FacetEventListener listener) {
		synchronized(facetEventListeners) {
			if (facetEventListeners.remove(listener)) {
				// deregister interest in health events from existing listener
				if (listener instanceof FacetHealthSender) {
					((FacetHealthSender)listener).removeFacetHealthListener(this);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	private void send(FacetEvent event) {
		synchronized(facetEventListeners) {
			for (FacetEventListener listener : facetEventListeners) {
				listener.facetEvent(event);
			}
		}
	}
	
	protected final void send(String methodName, Object[] args) {
		
		FacetEvent event = new FacetEvent();
		event.setMeemPath(meemPath);
		event.setFacetId(facetId);
		event.setFacetClass(facetClass.getName());
		event.setMethod(methodName);
		event.setParams(args);

		send(event);
	}

	public void contentSent() {
		FacetHealthEvent event = new FacetHealthEvent();
		event.setMeemPath(meemPath);
		event.setFacetId(facetId);
		event.setFacetClass(facetClass.getName());
		event.setBindingState(FacetHealthEvent.FACET_RESOLVED);
		
		facetHealthEvent(event);
	}
	
	public void contentFailed(String reason) {
		FacetHealthEvent event = new FacetHealthEvent();
		event.setMeemPath(meemPath);
		event.setFacetId(facetId);
		event.setFacetClass(facetClass.getName());
		event.setBindingState(FacetHealthEvent.FACET_NOTRESOLVED);
		facetHealthEvent(event);
	}
	
}
