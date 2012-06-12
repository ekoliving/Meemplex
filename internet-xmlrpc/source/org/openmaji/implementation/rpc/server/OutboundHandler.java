/*
 * Created on 14/09/2004
 *
 */
package org.openmaji.implementation.rpc.server;


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.openmaji.implementation.rpc.binding.util.MeemPathHelper;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.rpc.binding.BindingFactory;
import org.openmaji.rpc.binding.FacetEventListener;
import org.openmaji.rpc.binding.FacetHealthEvent;
import org.openmaji.rpc.binding.FacetHealthListener;
import org.openmaji.rpc.binding.OutboundBinding;

/**
 * This class represents a handler for a particular MeemPath, Facet pair.
 * The Facet should be an Outbound Facet.
 * 
 * The task of this class is to 
 * - Resolve the Meem
 * - Add a reference to the Binding to the Meems outbound Facet
 * - Receive messages from the Facet (via the binding) and send these outbound messages to the SessionHandler 
 *   (they become inbound messages to the client)
 * 
 * When this object is setup:
 * 1. Create the Binding object
 * 2. Resolve the Meem
 * 3. Add a reference to the Binding to the Meems outbound Facet
 * 
 * When this object is cleaned up:
 * 1. Remove reference to the Binding from the Meem
 * 
 * TODO make sure invocations on Meems occur in Managed threads
 * TODO should allow the RPC client to specify "contentRequired" and "filters"?
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundHandler <T extends Facet> extends FacetHandler<T> {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean DEBUG = false;
	
	/** the binding object */
	private final OutboundBinding binding;
	
	/** the maji proxy to the binding */
	private Facet proxy;
	
	/** a reference to the facet on the proxy */
	private Reference reference;
	

	/**
	 * Constructor
	 * 
	 * @param meemPath
	 * @param facetId
	 * @param facetClass
	 */
	OutboundHandler(Subject subject, MeemPath meemPath, String facetId, Class<T> facetClass) {
		super(subject, meemPath, facetId, facetClass);
		
		if (DEBUG) {
			logger.log(Level.INFO, "Creating new OutboundHandler for " + meemPath + ":" + facetId);
		}
		
		binding = BindingFactory.getOutboundBinding(facetClass);
		if (binding == null) {
			throw new RuntimeException("Could not locate outbound binding for class: " + facetClass);
		}
		binding.setMeemPath(MeemPathHelper.fromMeemPath(meemPath));
		binding.setFacetId(facetId);
		
		// listen for health events from binding
		binding.addBindingHealthListener(
				new FacetHealthListener() {
					public void facetHealthEvent(FacetHealthEvent event) {
						if (event.getBindingState() == FacetHealthEvent.UNKNOWN) {
							event.setBindingState(bindingState);
						}
						else {
							bindingState = event.getBindingState();
						}
						event.setLifeCycleState(lifeCycleState.getCurrentState());
						OutboundHandler.this.sendHealth(event);
					}
				}
		);
		
		resolveMeem(meemPath);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public synchronized void addFacetEventListener(FacetEventListener listener) {
		binding.addFacetEventListener(listener);
	}

	public synchronized void removeFacetEventListener(FacetEventListener listener) {
		binding.removeFacetEventListener(listener);
	}
	
	/**
	 * 
	 *
	 */
	protected synchronized void cleanup() {
		if (DEBUG) {
			logger.log(Level.INFO, "cleanup handler for: " + meemPath + " : " + meem);
		}
		
		super.cleanup();

		removeMeemReference();
		
		// cleanup proxy
		cleanupProxy();
	}
	
	/**
	 * TODO how do we send a BindingState of FacetResolved, when we don't know?
	 * TODO is there a way we can tell whether the Facet exists on the Meem?
	 * 
	 * @param meem
	 */
	protected synchronized void meem(Meem meem) {
		if (DEBUG) {
			logger.log(Level.INFO, "got meem for: " + meemPath + " : " + meem);
		}

		removeMeemReference();
		
		super.meem(meem);

		if (meem == null) {
			//cleanupProxy();
		}
		else {
			// add a reference to this handler to the meem.
			meem.addOutboundReference(getReference(), false);
		}
	}

	private void removeMeemReference() {
		// remove reference
		if (meem != null && reference != null) {
			meem.removeOutboundReference(reference);
		}
	}
	
	private Reference getReference() {
		if (proxy == null) {
			if (DEBUG) {
				logger.log(Level.INFO, "creating new proxy for binding: " + binding);
			}
			proxy = serverGateway.getTargetFor((T)binding, facetClass);
			reference = null;	// reset reference
		}
		if (reference == null) {
			if (DEBUG) {
				logger.log(Level.INFO, "creating new reference for: " + meemPath + " : " + facetId);
			}
			reference = Reference.spi.create(facetId, proxy, true);
		}
		
		return reference;
	}

	private void cleanupProxy() {
		if (proxy != null) {
			serverGateway.revokeTarget(proxy, binding);
			proxy = null;
		}		
	}


}
