/*
 * Created on 19/10/2004
 */
package org.openmaji.implementation.rpc.server;

import javax.security.auth.Subject;



import java.util.logging.Level;
import java.util.logging.Logger;


import org.openmaji.implementation.rpc.binding.util.MeemPathHelper;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.rpc.binding.FacetHealthEvent;
import org.openmaji.rpc.binding.FacetHealthListener;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.resolver.MeemResolverClient;

/**
 * @author Warren Bloomer
 *
 */
public class FacetHandler <T extends Facet> {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static boolean DEBUG = false;
	
	protected final MeemPath meemPath;
	
	protected final String   facetId;
	
	protected final Class<T>    facetClass;

	protected final ServerGateway serverGateway;

	/** the meem to bind to */
	protected Meem     meem;
	
	private FacetHealthListener       healthListener;

	private FacetHealthEvent          lastHealthEvent; 
	
	private LifeCycleClientImpl        lifeCycleClient;

	private Reference<LifeCycleClient> lifeCycleReference;
	
	private MeemRegistryClientImpl     meemRegistryClient;
	
	private Reference<MeemRegistryClient> meemRegistryClientReference;
	
	private MeemPathResolverClientImpl meemPathResolverClient;
	
	private Reference<MeemResolverClient> meemPathResolverClientReference;
	
	/** the current binding state */
	protected int                      bindingState   = FacetHealthEvent.UNKNOWN;
	
	/** the last received lifecycle state */
	protected LifeCycleState           lifeCycleState = null;

	private Meem meemResolverMeem = null;

	private Meem meemRegistryMeem = null;
	
	
	/**
	 * Constructor.
	 * 
	 * @param subject
	 * @param meemPath
	 * @param facetId
	 * @param facetClass
	 */
	public FacetHandler(Subject subject, MeemPath meemPath, String facetId, Class<T> facetClass) {
		this.meemPath      = meemPath;
		this.facetId       = facetId;
		this.facetClass    = facetClass;
		this.serverGateway = ServerGateway.spi.create(subject);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public synchronized void setFacetHealthListener(FacetHealthListener listener) {
		this.healthListener = listener;
		
		// send last health event
		if (lastHealthEvent != null) {
			sendHealth(lastHealthEvent);
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	protected synchronized void sendHealth(FacetHealthEvent event) {
		
		if (healthListener != null) {
			healthListener.facetHealthEvent(event);
		}
		lastHealthEvent = event;
	}


	/**
	 * Meem has been resolved.
	 * 
	 * @param meem
	 */
	protected void meem(Meem meem) {
		if (DEBUG) {
			logger.log(Level.INFO, "got meem for " + meemPath + ": " + meem);
		}
		// remove any existing reference to lifecycleclient
		removeMeemReferences();

		this.meem = meem;
		
		if (meem == null) {
			this.bindingState = FacetHealthEvent.MEEM_NOTRESOLVED;
			FacetHealthEvent event = newHealthEvent();
			sendHealth(event);
		}
		else {
			this.bindingState = FacetHealthEvent.MEEM_RESOLVED;
			FacetHealthEvent event = newHealthEvent();
			sendHealth(event);
			addMeemReferences();
		}
	}
	
	/**
	 * 
	 */
	protected void cleanup() {
		removeReferences();
		
		// cleanup references
		lifeCycleReference = null;
		meemPathResolverClientReference = null;
		meemRegistryClientReference = null;

		// cleanup proxies
		
		if (lifeCycleClient != null) {
			lifeCycleClient.cleanup();
			lifeCycleClient    = null;
		}
		if (meemPathResolverClient != null) {
			meemPathResolverClient.cleanup();
			meemPathResolverClient = null;
		}
		if (meemRegistryClient != null) {
			meemRegistryClient.cleanup();
			meemRegistryClient = null;
		}
	}
	
	/**
	 * 
	 * @param meemPath
	 */
	protected void resolveMeem(MeemPath meemPath) {
		if (DEBUG) {
			logger.log(Level.INFO, "resolving meem for: " + meemPath + " : " + facetId);
		}
		if (meemPath.isDefinitive()) {
			resolveDefinitive(meemPath);
		}
		else {
			resolveHyperSpace(meemPath);
		}
	}
	
	private void removeMeemReferences() {
		if (this.meem != null) {
			if (lifeCycleReference != null) {
				this.meem.removeOutboundReference(lifeCycleReference);
			}
		}		
	}
	
	private void removeReferences() {
		removeMeemReferences();
		
		if (meemResolverMeem != null && meemPathResolverClientReference != null) {
			meemResolverMeem.removeOutboundReference(meemPathResolverClientReference);
		}
		if (meemRegistryMeem != null && meemRegistryClientReference != null) {
			meemRegistryMeem.removeOutboundReference(meemRegistryClientReference);
		}
	}
	
	private void addMeemReferences() {
		if (this.meem != null) {
			// Facet and Proxy Facet for receiving lifecycle state from the Meem
			if (lifeCycleClient == null) {
				lifeCycleClient = new LifeCycleClientImpl();
				lifeCycleReference = Reference.spi.create(
					"lifeCycleClient", 				// facet id
					lifeCycleClient.getProxy(), 	// target proxy
					true							// content required
				);
			}
	
			if (lifeCycleReference != null) {
				this.meem.addOutboundReference(lifeCycleReference, false);
			}
		}
		/*
		if (meemResolverMeem != null && meemPathResolverClientReference != null) {
			meemResolverMeem.addOutboundReference(meemPathResolverClientReference, false);
		}
		if (meemRegistryMeem != null && meemRegistryClientReference != null) {
			meemRegistryMeem.addOutboundReference(meemRegistryClientReference, false);
		}
		*/
	}

	/**
	 * Resolve a Meem from a 
	 * @param meemPath
	 */
	private void resolveDefinitive(MeemPath meemPath)
	{
		if (DEBUG) {
			logger.log(Level.INFO, "resolving meem via registry for: " + meemPath);
		}
		
		// Facet and Proxy Facet for receiving messages from the resolver Meem
		meemRegistryClient  = new MeemRegistryClientImpl();

		meemRegistryClientReference = Reference.spi.create(
				"meemRegistryClient", 
				meemRegistryClient.getProxy(), 
				true, 
				ExactMatchFilter.create(meemPath)
			);
		
		MeemPath meemRegistryMeemPath = 
			MeemPath.spi.create(
					Space.HYPERSPACE, 
					MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemRegistry"
				);
		
		meemRegistryMeem = serverGateway.getMeem(meemRegistryMeemPath);
		
		meemRegistryMeem.addOutboundReference(meemRegistryClientReference, false);
	}

	/**
	 * Resolve a Meem from a Hyperspace path
	 * 
	 * @param meemPath
	 */
	private void resolveHyperSpace(MeemPath meemPath)
	{
		if (DEBUG) {
			logger.log(Level.INFO, "resolving meem via hyperspace for: " + meemPath);
		}
		
		// Facet and Proxy Facet for receiving messages from the resolver Meem
		meemPathResolverClient  = new MeemPathResolverClientImpl();

		meemPathResolverClientReference = Reference.spi.create(
				"meemResolverClient",
				meemPathResolverClient.getProxy(),
				true,
				ExactMatchFilter.create(meemPath));

		MeemPath meemResolverMeemPath = 
			MeemPath.spi.create(
					Space.HYPERSPACE, 
					MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemResolver"
				);

		meemResolverMeem = serverGateway.getMeem(meemResolverMeemPath);

		meemResolverMeem.addOutboundReference(meemPathResolverClientReference, false);
	}
	
	/**
	 * 
	 */
	protected FacetHealthEvent newHealthEvent() {
		FacetHealthEvent event = new FacetHealthEvent();
		if (meem != null) {
			event.setMeemId(meem.getMeemPath().toString());
		}
		event.setMeemPath(MeemPathHelper.fromMeemPath(meemPath));
		event.setFacetId(facetId);
		event.setFacetClass(facetClass.getName());
		event.setBindingState(bindingState);
		if (lifeCycleState != null) {
			event.setLifeCycleState(lifeCycleState.getCurrentState());
		}

		return event;
	}
	
	/**
	 * 
	 */
	public class MeemRegistryClientImpl implements MeemRegistryClient, ContentClient
	{
		private MeemRegistryClient proxy;
		
		public MeemRegistryClient getProxy() {
			if (proxy == null) {
				proxy   = serverGateway.getTargetFor(this, MeemRegistryClient.class);
			}
			return proxy;
		}
		
		public void meemRegistered(Meem meem) {
			meem(meem);
		}

		public void meemDeregistered(Meem meem) {
			meem(null);
		}

		public void contentSent() {
			//cleanup();
		}

		public void contentFailed(String reason) {
			logger.log(Level.INFO, "Could not resolve meem: " + reason);
			//cleanup();
		}
		
		private void cleanup() {
			serverGateway.revokeTarget(proxy, this);
			proxy = null;
		}
	}

	/**
	 * MeemPathResolverClientImpl
	 */
	public class MeemPathResolverClientImpl implements MeemResolverClient, ContentClient
	{
		private MeemResolverClient proxy;
		
		public MeemResolverClient getProxy() {
			if (proxy == null) {
				proxy   = serverGateway.getTargetFor(this, MeemResolverClient.class);
			}
			return proxy;
		}
		
		public void meemResolved(MeemPath meemPath, Meem meem) {
			if (DEBUG) {
				logger.log(Level.INFO, "MeemPathResolverClientImpl meemResolved: " + meemPath + " -> " + meem);
			}
			if (meem == null) {
				//meem = null;
				//System.err.println("why is resolved meem null?");
			}
			else {
				meem(meem);
			}
		}

		public void contentSent() {
			if (DEBUG) {
				logger.log(Level.INFO, "MeemPathResolverClientImpl contentSent.");
			}
			//cleanup();
		}

		public void contentFailed(String reason) {
			if (DEBUG) {
				logger.log(Level.INFO, "MeemPathResolverClientImpl Could not resolve meem: " + reason);
			}
			//cleanup();
		}
		
		private void cleanup() {
			serverGateway.revokeTarget(proxy, this);
			proxy = null;
		}
	}
	
	/**
	 * LifeCycleClientImpl
	 */
	public class LifeCycleClientImpl implements LifeCycleClient //, ContentClient
	{
		private LifeCycleClient proxy;
		
		public LifeCycleClient getProxy() {
			if (proxy == null) {
				proxy   = serverGateway.getTargetFor(this, LifeCycleClient.class);
			}
			return proxy;
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {			
		}
		
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			lifeCycleState = transition.getCurrentState();

			/// handle a meem that has been removed
			if (LifeCycleState.ABSENT.equals(lifeCycleState)) {
				if (DEBUG) {
					logger.log(Level.INFO, "meem is absent: " + meemPath);
				}
				meem(null);
			}

			FacetHealthEvent event = newHealthEvent();
			sendHealth(event);			
		}
		
		public void cleanup() {
			if (proxy != null) {
				serverGateway.revokeTarget(proxy, this);
				proxy = null;
			}
		}
	}
}
