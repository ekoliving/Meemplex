/*
 * Created on 14/09/2004
 *
 */
package org.openmaji.implementation.rpc.server;

import java.util.Vector;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.openmaji.implementation.rpc.binding.util.MeemPathHelper;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.FacetDescriptor;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.rpc.binding.BindingFactory;
import org.openmaji.rpc.binding.FacetEvent;
import org.openmaji.rpc.binding.FacetEventListener;
import org.openmaji.rpc.binding.FacetHealthEvent;
import org.openmaji.rpc.binding.FacetHealthListener;
import org.openmaji.rpc.binding.InboundBinding;
import org.openmaji.system.meem.wedge.reference.ContentClient;

/**
 * Create Binding object
 * Resolve Meem
 * Resolve Meem Facet Reference
 * Add Facet to Binding listeners
 * 
 * 
 * TODO make sure invocations on Meems occur in Managed threads
 * 
 * @author Warren Bloomer
 *
 */
public class InboundHandler extends FacetHandler implements FacetEventListener {
	private static final Logger logger = Logger.getAnonymousLogger();

	/** reference to the target Facet */
	private Facet    targetFacet;
	
	private InboundBinding binding;
	
	/** for invocations made before the target is resolved */
	private Vector<FacetEvent> queue = new Vector<FacetEvent>();
	
	
	/**
	 * Constructor
	 * 
	 * @param meemPath
	 * @param facetId
	 * @param facetClass
	 */
	public InboundHandler(Subject subject, MeemPath meemPath, String facetId, Class<? extends Facet> facetClass) {
		super(subject, meemPath, facetId, facetClass);
		
		binding = BindingFactory.getInboundBinding(facetClass);
		if (binding == null) {
			throw new RuntimeException("Could not locate inbound binding for class: " + facetClass);
		}
		binding.setMeemPath(MeemPathHelper.fromMeemPath(meemPath));
		binding.setFacetId(facetId);
		
		// listen for health events from binding
//		binding.addBindingHealthListener(
//				new FacetHealthListener() {
//					public void facetHealthEvent(FacetHealthEvent event) {
//						if (event.getBindingState() == FacetHealthEvent.UNKNOWN) {
//							event.setBindingState(bindingState);
//						}
//						else {
//							bindingState = event.getBindingState();
//						}
//						event.setLifeCycleState(lifeCycleState.getCurrentState());
//						InboundHandler.this.sendHealth(event);
//					}
//				}
//		);

		resolveMeem(meemPath);
	}

	
	/**
	 * To get a target Facet
	 * 
	 */
	public void resolveTarget(Meem meem, String facetIdentifier, Class<? extends Facet> specification)
	{
		MeemClientImpl meemClient  = new MeemClientImpl();

		Facet proxy               = meemClient.getProxy();
		Filter filter             = new FacetDescriptor(facetIdentifier, specification);
		Reference targetReference = Reference.spi.create("meemClientFacet", proxy, true, filter);

		// add a reference that automatically gets removed after content is sent
		meem.addOutboundReference(targetReference, true);
	}

	/**
	 * 
	 */
	protected void cleanup() {
		super.cleanup();
	}
	
	/* ------------------ FacetEventListener interface ------------- */
	
	/**
	 * 
	 * @param event
	 */
	public void facetEvent(FacetEvent event) {

		//System.out.println("got inbound facet event: " + event);
		
		// TODO queue until listeners have been added, i.e. references resolved
		
		if (targetFacet == null) {
			// then queue the event
			synchronized(queue) {
				queue.add(event);
			}
		}
		else {
			binding.facetEvent(event);
		}
	}

	/* ----------------------- utilities ---------------- */

	/**
	 * 
	 * @param meem
	 */
	protected void meem(Meem meem) {
		super.meem(meem);
		
		if (meem == null) {
			targetFacet(null);
		}
		else {
			// now get the reference to the Facet
			resolveTarget(meem, facetId, facetClass);
		}
	}
	
	/**
	 * Set the target facet on which to send messages.
	 * 
	 * @param facet the target facet.
	 */
	private void targetFacet(Facet facet) {
		
		binding.removeListener(this.targetFacet);		
		this.targetFacet = facet;
		
		// send binding state
		if (facet == null) {
			bindingState = FacetHealthEvent.FACET_NOTRESOLVED;
		}
		else {
			binding.addListener(facet);
			bindingState = FacetHealthEvent.FACET_RESOLVED;			
		}

		FacetHealthEvent facetHealthEvent = newHealthEvent();
		sendHealth(facetHealthEvent);
		
		if (facet != null) {
			// empty queue
			sendFacetEvents();
		}
	}
	
	private void sendFacetEvents() {
		synchronized (queue) {
			while (queue.size() > 0) {
				FacetEvent event = (FacetEvent) queue.remove(0);
				binding.facetEvent(event);
			}
		}		
	}
	
	/**
	 * Receies the appropriate target facet of the target meem that
	 * this handler will send messages to.
	 */
	public class MeemClientImpl implements MeemClient, ContentClient {

		private Facet proxy;

		public Facet getProxy() {
			if (proxy == null) {
				proxy = serverGateway.getTargetFor(this, MeemClient.class);
			}
			return proxy;
		}
		
		public void referenceRemoved(Reference reference) {
			targetFacet(null);
		}

		public void referenceAdded(Reference reference) {
			//LogTools.info(logger, "got reference: " + reference);
			
			targetFacet(reference.getTarget());
		}

		public void contentSent() {
			cleanup();
		}

		public void contentFailed(String reason) {
			//if (trace) {
				logger.info("failed to get reference: " + reason);
			//}
			cleanup();
		}
		
		private void cleanup() {
			serverGateway.revokeTarget(proxy, this);
			proxy = null;
		}
	}
		
}
