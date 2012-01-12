/*
 * @(#)FacetProxy.java
 * Created on 30/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.util.LinkedList;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.gateway.ServerGateway;




/**
 * <code>FacetProxy</code> provides generic infrastructure for a facet proxy, 
 * which supplies the implementation of a provider (Inbound) and its 
 * corresponding client (Outbound) to be used in the context of 
 * <code>MeemClientProxy</code>.
 * <p>
 * User of <code>FacetProxy</code> can freely define the required support of a 
 * facet proxy through <code>FacetSpecificationPair</code>, or they can simply 
 * use the default specification, usually declared in the derived classes.
 * <p>
 * Implementor of derived class of <code>FacetProxy</code> only have to focus on
 * providing facet pair specific caching. 
 * <p>
 * @author Kin Wong
 * @see org.openmaji.implementation.tool.eclipse.client.FacetSpecificationPair
 * @see org.openmaji.implementation.tool.eclipse.client.MeemClientProxy
 * @see org.openmaji.implementation.tool.eclipse.client.ClientSynchronizer
 */
abstract public class FacetProxy {
	private static final long serialVersionUID = 6424227717462161145L;

	static private Object[] EMPTY_CLIENT = new Object[0];
	
	private MeemClientProxy meemClientProxy;// meem client proxy container
	private FacetSpecificationPair specs;		// Specification of the references (in/out)
	protected boolean connected = false;			// The facet proxy has been connected?
	protected Reference outboundReference;	// Reference to the outbound facet
	protected Object inboundReference;			// Reference to the inbound facet
	
	protected LinkedList clients;	// All clients that implements the outbound facet
	protected boolean contentInitialized = false; // Whether it has been initialised.
	
	/**
	 * Constructs an instance of <code>FacetProxy</code>.<p>
	 * @param meemClientProxy The meem client proxy that contains this facet 
	 * proxy.
	 * @param specs The specifications of facets that defines both inbound and
	 * outbound references.
	 * @see MeemClientProxy
	 */
	protected FacetProxy(
		MeemClientProxy meemClientProxy, 
		FacetSpecificationPair specs) {
		this.meemClientProxy = meemClientProxy;
		this.specs = specs;
		
		clearContent();
	}
	
	/**
	 * Gets the specifications of facets pair that defines both inbound and 
	 * outbound references.<p>
	 * @return FacetSpecificationPair The specifications of facets pair that 
	 * defines both inbound and outbound references.
	 */
	public FacetSpecificationPair getFacetSpecificationPair() {
		return specs;
	}
	
	/**
	 * Gets the meem client proxy that contains this facet proxy.<p>
	 * @return MeemClientProxy The meem client proxy that contains this facet
	 * proxy.
	 */
	protected MeemClientProxy getMeemClientProxy() {
		return meemClientProxy;
	}
	
	/**
	 * Gets the UI synchronizer associates with this facet proxy.<p>
	 * @return ClientSynchronizer The UI synchronizer associates with this facet 
	 * proxy.
	 * @see ClientSynchronizer
	 */
	protected ClientSynchronizer getSynchronizer() {
		return getMeemClientProxy().getSynchronizer();
	}
	
	/**
	 * Gets whether this facet proxy is read only.<p>
	 * A facet proxy is defined as read-only when the inbound reference is NOT
	 * established.<p>
	 * @return true if this facet proxy is read only, false otherwise.
	 */
	public boolean isReadOnly() {
		return (inboundReference == null);
	}
	
	/**
	 * Gets whether this facet proxy is modifiable.<p>
	 * A facet proxy is defined as modifiable when the inbound reference is 
	 * established. <p>
	 * @return true if this facet proxy is modifiable, false otherwise.
	 */
	public boolean isModifiable() {
		return (inboundReference != null);
	}
	
	/**
	 * Checks whether the facet proxy is connected.<p>
	 * @return true is the facet proxy is connect, false otherwise.
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Gets whether this facet proxy is available.<p>
	 * Availability is defined as the state in which at least on of the references 
	 * (inbound or outbound) is established.<p>
	 * @return true if this facet proxy is available, false otherwise.
	 */
	public boolean isAvailable() {
		return (outboundReference != null) || (inboundReference != null);
	}
	
	/**
	 * Gets the underlying meem from the connected <code>MeemClientProxy</code>.
	 * <p>
	 * @return The underlying meem from the connected
	 * <code>MeemClientProxy</code>.
	 */
	protected Meem getUnderlyingMeem() {
		return getMeemClientProxy().getUnderlyingMeem();
	}
	
	/**
	 * Gets the inbound facet reference.<p>
	 * @return The inbound facet reference if it is bound, null otherwise.
	 */
	protected Object getInboundReference() {
		return inboundReference;
	}

	/**
	 * Returns an object that implements the outbound client interface of the type 
	 * defined in the outbound facet specification of the facet specification 
	 * pair.<p> 
	 * Derived class must override this by returning another an 
	 * implementation, most often a field that implements the client interface.<p>
	 * @return Object An object that implements the outbound interface.
	 */
	abstract protected Facet getOutboundTarget();	
	
	/**
	 * Returns whether the content of this proxy has been initialized.<p>
	 * @return boolean The content of this proxt has been initialized, false 
	 * otherwise.
	 */
	public boolean isContentInitialized() {
		return contentInitialized;
	}
	
	/**
	 * Sets whether the content of this proxy has been initialised.<p>
	 * @param contentInitialized true if the content of this proxy has been
	 * fully initialized, false otherwise.
	 */
	protected void setContentInitialize(boolean contentInitialized) {
		this.contentInitialized = contentInitialized;
	}
	
	/**
	 * Adds an outbound facet implementation to the client list.<p>
	 * When the outbound facet implementation of this facet proxy is executable,
	 * the client implementation will also be invoked from the thread defined by
	 * <code>ClientSynchronizer</code> associates with the 
	 * <code>MeemClientProxy</code>. The client lisrt is first-come-last-serve.<p>
	 * @param client A client to be added to the client list.
	 * @see ClientSynchronizer
	 * @see MeemClientProxy
	 */
	public void addClient(Object client) {
		if(client == null) return;
		Class expectedOutboundType = 
			getFacetSpecificationPair().getOutboundSpecification().getType();
			
		if(!expectedOutboundType.isAssignableFrom(client.getClass())) {
			throw new ClassCastException(
					"client is " + client.getClass() + ", must be instance of " + expectedOutboundType
				);
		}
														
		if(clients == null) { clients = new LinkedList(); }
		clients.addFirst(client);
		
		// Make contentRequired possible for client added after connect!
		// Since it has been connected already, simulate a contentRequired here.
		//if(isConnected()) {
		//	realizeClientContent(client);
		//}
	}
	
	/**
	 * Removes an outbound facet implementation from the client list previously
	 * added by <code>addClient()</code>.<p>
	 * @param client A client to be removed from the client list.
	 * @return true if the client has been removed, false owtherwise.
	 * @see ClientSynchronizer
	 * @see MeemClientProxy
	 */
	public boolean removeClient(Object client) {
		if((client == null)||(clients == null)) return false;

		// Make cleanup possible for client added after connect!
		//if(isConnected())
		//clearClientContent(client);
		
		if(clients.remove(client)) {
			if(clients.isEmpty() && (!isEssential())) {
				// Lets remove the 
				getMeemClientProxy().removeFacetProxy(this);
				 clients = null; 
			} 
			return true;
		}
		return false;
	}
	
	/**
	 * Connects this facet proxy to the facet pair of the underlying meem based
	 * on the facet specification pair.<p>
	 */
	protected void connect() {
		if(isConnected()) return;
		connectOutbound();
		connected = connectInbound();
		//connected = true;
	}
	
	/**
	 * Disconnects this facet proxy from the facet pair of the underlying meem 
	 * based on the facet specification pair.<p>
	 */
	protected void disconnect() {
		if(!isConnected()) return;
		disconnectInbound();
		disconnectOutbound();
		connected = false;	// disconnect Now!
		// Should tell client to clear!
		clear();
	}
	
	protected void clear() {
		clearClientContentAll();
		clearContent();
	}
	
	/**
	 * Resets the cached context to client
	 * <p>
	 *
	 */
	abstract protected void realizeClientContent(Object client);
	abstract protected void clearClientContent(Object client);
	abstract protected void clearContent();
	
	/**
	 * Resets all clients connected to this facet proxy.<p>
	 */
	protected void realizeClientContentAll() {
		Object[] clients = getClients();
		for(int i = 0; i < clients.length; i++) {
			realizeClientContent(clients[i]);
		}
	}
	
	/**
	 * Clears contents of all clients connected to this facet proxy.<p>
	 */	
	protected void clearClientContentAll() {
		Object[] clients = getClients();
		for(int i = 0; i < clients.length; i++) {
			clearClientContent(clients[i]);
		}
	}
	
	/**
	 * Returns whether this facet proxy is essential.<p>
	 * A facet proxy is essential when connection is made to the underlying meem
	 * regardless whether it is presented in the MetaMeem. Derived class of 
	 * facet proxy can freely change this.<p>
	 * @return true if this facet proxy is essential, false otherwise.
	 */
	protected boolean isEssential() {
		return false;
	}
	
	/**
	 * Connects the outbound implementation of this facet proxy to the facet in 
	 * the underlying meem.<p>
	 */
	protected void connectOutbound() {
		
		if(getUnderlyingMeem() == null) return;	// The proxy is not associated with a meem
		
		if(outboundReference != null) return; // Has already connected
		
		final Meem meem = getUnderlyingMeem();
		final FacetOutboundSpecification outbound = specs.getOutboundSpecification();
		
		if(!isEssential()) {
			final MetaMeemProxy metaMeemProxy = getMeemClientProxy().getMetaMeem();
			if (metaMeemProxy == null) {
				return;
			}
			
			if (metaMeemProxy.isContentInitialized() == false) {
				/*
				Runnable runnable = new Runnable() {
					public void run() {
						while (!metaMeemProxy.isContentInitialized()) {
							try {
								Thread.sleep(250);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						System.err.println("metaMeemProxy not initialized !!: " + this + " : " + metaMeemProxy);
						
						if (!metaMeemProxy.hasA(outbound.getIdentifier(), outbound.getType(), Direction.OUTBOUND)) {
							return;
						}
						
						connectOutbound(meem, outbound);		
					}
				};
				*/
				return;
			}
			
			if (!metaMeemProxy.hasA(outbound.getIdentifier(), outbound.getType(), Direction.OUTBOUND)) {
				return;
			}
				
		}		
		connectOutbound(meem, outbound);		
	}
	
	private void connectOutbound(Meem meem, FacetOutboundSpecification outbound) {
		// wraps target up for Maji invocation
		Facet target = SecurityManager.getInstance().getGateway().getTargetFor(getOutboundTarget(), outbound.getType());
		outboundReference = Reference.spi.create(outbound.getIdentifier(), target, true);
		
		// add the reference to the target to the meem
		meem.addOutboundReference(outboundReference, false);
	}
	
	/**
	 * Disconnects the outbound implementation of this facet proxy from the 
	 * underlying meem.
	 * <p>
	 */
	protected void disconnectOutbound() {
		
		if(outboundReference == null) return;
		
		GatewayManagerWedge.revokeTarget(outboundReference.getTarget(), getOutboundTarget());
		getUnderlyingMeem().removeOutboundReference(outboundReference);
		outboundReference = null;
	}
	
	/**
	 * Connects the inbound implementation of this facet proxy to the inbound 
	 * facet declared in the inbound facet specification of the underlying meem.
	 * <p>
	 */
	protected boolean connectInbound() {
	
		Meem meem = getUnderlyingMeem();
		
		if(meem == null) {
			// Nothing to connect
			return true;	
		}
		
		if(specs.isReadOnly()) {
			// Specification wants read-only proxy.
			return true;	
		}
		
		if(SecurityManager.getInstance().isGuest()) {
			// HACK
			return true;
		}
		
		if( !isEssential() && !getMeemClientProxy().isActive() ) {
			return false;
		}
				
		FacetInboundSpecification inbound = specs.getInboundSpecification();
		
		// only allow LifeCycleClient to connect if not active
		if (!getMeemClientProxy().isActive() && !inbound.getType().equals(LifeCycleClient.class)) {
//			System.err.println("FacetProxy.connectInbound(): active" + getMeemClientProxy().isActive() + " : " + inbound.getType().getName() + " : " + getMeemClientProxy());
			return false;
		}
		
		ServerGateway serverGateway = SecurityManager.getInstance().getGateway();

		// TODO replace this with asynchronous method to get the Target
		inboundReference = serverGateway.getTarget(meem, inbound.getIdentifier(), inbound.getType());
		
		return true;
//		serverGateway.getTarget(meem, inbound.getIdentifier(), inbound.getType(), new Callback());		
	}

	/**
	 *Disconnects the inbound implementation previously connected by 
	 *<code>connectInbound()</code>.<p>
	 */
	protected void disconnectInbound() {
		inboundReference = null;
	}

	/**
	 * Gets whether the client list contains client(s).<p>
	 * @return boolean true is the client list contains client, false otherwise.
	 */
	public boolean containsClient() {
		return clients != null;
	}
	
	/**
	 * Gets an array that contains all clients.
	 * @return Object[] An array contains all clients.
	 */
	public Object[] getClients() {
		if(clients == null) return EMPTY_CLIENT;
		return clients.toArray();
	}
/*
	private final class Callback implements FacetConsumer, ContentClient {

		public void facet(Meem meem, String facetId, Facet facet) {
			inboundReference = facet;
			// TODO send ok
		}

		public void contentFailed(String reason) {
			LogTools.info(logger, "Failed to get facet target: " + reason);
		}

		public void contentSent() {
		}
	}
*/	
}
