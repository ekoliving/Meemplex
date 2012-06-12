/*
 * @(#)MeemUnbound.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.meem;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.server.meem.wedge.remote.SmartProxyMeem;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.resolver.MeemResolver;
import org.openmaji.system.space.resolver.MeemResolverClient;


import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * <p>
 * MeemUnbound describes an unbound meem.
 * </p>
 */
public class MeemUnbound implements Meem, Serializable {
	
	private static final long serialVersionUID = 901389287298742L;

	private static final Logger logger = Logger.getAnonymousLogger();

	private final MeemPath meemPath;

	/** this is a static cache of unbound meems */ 
	private static Map<MeemPath, MeemUnbound> unboundMeems = new HashMap<MeemPath, MeemUnbound>();

	/** the bound meem. This will be set when the meemis bound */
	private transient Meem meem;

	private transient Client client = null;
	
	private transient LifeCycleClientImpl lifeCycleClientImpl = null;

	private transient LifeCycleTransition lastTransition = LifeCycleTransition.DORMANT_ABSENT;

	private transient Map<Reference, RequestStack> lifeCycleClientRefs = null;


	private transient Meem queuedMeemProxy = (Meem) Proxy.newProxyInstance(
			this.getClass().getClassLoader(), 
			new Class[] { Meem.class },
			getClient()
		);


	/* ------------------------- static methods --------------------------- */
	
	public static Meem getInstance(MeemPathImpl meemPath) {
		return resolve(meemPath);
	}

	public static Meem getInstance(MeemPath meemPath, Meem registryMeem) {
		return resolve(meemPath);
	}

	private static synchronized MeemUnbound resolve(MeemPath meemPath) {
		MeemUnbound mu = (MeemUnbound) unboundMeems.get(meemPath);
		if (mu == null) {
			mu = new MeemUnbound(meemPath);
			unboundMeems.put(meemPath, mu);
		}
		return mu;
	}


	/* ------------------------- Meem interface --------------------------- */
	
	/**
	 * @see org.openmaji.meem.Meem#getMeemPath()
	 */
	public MeemPath getMeemPath() {
		return meemPath;
	}

	public void addDependency(Facet facet, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		Client client = getClient();
		synchronized (client) {
			if (!client.isDone()) {
				queuedMeemProxy.addDependency(facet, dependencyAttribute, lifeTime);
				return;
			}
		}

		doAddDependency(meem, facet, dependencyAttribute, lifeTime);
	}
	
	public void addDependency(String facetId, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		Client client = getClient();
		synchronized (client) {
			if (!client.isDone()) {
				queuedMeemProxy.addDependency(facetId, dependencyAttribute, lifeTime);
				return;
			}
		}

		doAddDependency(meem, facetId, dependencyAttribute, lifeTime);
	}
	
	public void removeDependency(DependencyAttribute dependencyAttribute) {
		Client client = getClient();
		synchronized (client) {
			if (!client.isDone()) {
				queuedMeemProxy.removeDependency(dependencyAttribute);
				return;
			}
		}

		doRemoveDependency(meem, dependencyAttribute);
	}
	
	public void updateDependency(DependencyAttribute dependencyAttribute) {
		Client client = getClient();
		synchronized (client) {
			if (!client.isDone()) {
				queuedMeemProxy.updateDependency(dependencyAttribute);
				return;
			}
		}

		doUpdateDependency(meem, dependencyAttribute);
	}
	
	/**
	 * @see org.openmaji.meem.Meem#addOutboundReference(org.openmaji.meem.wedge.reference.Reference,
	 *      boolean)
	 */
	public void addOutboundReference(Reference reference, boolean automaticRemove) {
		if (reference == null) {
			throw new IllegalArgumentException("attempt to call addOutboundReference with null reference.");
		}
		
		Client client = getClient();
		synchronized (client) {
			if (!client.isDone()) {
				if (reference.getFacetIdentifier().equals("lifeCycleClient")) {
					addLifeCycleReference(reference, automaticRemove);
				}
				else {
					queuedMeemProxy.addOutboundReference(reference, automaticRemove);
				}
				return;
			}
		}

		doAddOutboundReference(meem, reference, automaticRemove);
	}

	/**
	 * @see org.openmaji.meem.Meem#removeOutboundReference(org.openmaji.meem.wedge.reference.Reference)
	 */
	public void removeOutboundReference(Reference reference) {
		if (reference == null) {
			throw new IllegalArgumentException("attempt to call removeOutboundReference with null reference.");
		}

		Client client = getClient();
		synchronized (client) {
			if (!client.isDone()) {
				if (reference.getFacetIdentifier().equals("lifeCycleClient")) {
					removeLifeCycleReference(reference);
				} 
				else {
					queuedMeemProxy.removeOutboundReference(reference);
				}
				return;
			}
		}

		doRemoveOutboundReference(meem, reference);
	}

	
	/* ---------------------------------- Object overrides ------------------------------ */
	
	public boolean equals(Object o) {
		return o == this || ((o instanceof Meem) && ((Meem) o).getMeemPath().equals(meemPath));
	}

	public int hashCode() {
		return meemPath.hashCode();
	}

	public String toString() {
		String boundString = getClient().isDone() ? "bound" : "unbound";

		return "MeemUnbound[" + this.getMeemPath().getLocation() + ", " + boundString + "]";
	}

	
	/* --------------------------------- private methods --------------------------------- */
	
	private synchronized Client getClient() {
		if (client == null) {
			client = new Client(this);
		}

		return client;
	}

	private Object readResolve() {
		return resolve(meemPath);
	}

	private MeemUnbound(MeemPath meemPath) {
		if (meemPath == null) {
			throw new IllegalArgumentException("MeemUnbound: MeemPath is null");
		}

		this.meemPath = meemPath;
	}

	private synchronized void doAddOutboundReference(Meem meem, Reference reference, boolean automaticRemove) {
		if (reference.getFacetIdentifier().equals("lifeCycleClient")) {
			addLifeCycleReference(reference, automaticRemove);
		} else if (meem != null) {
			meem.addOutboundReference(reference, automaticRemove);
		} else {
			ContentClient contentClient = MeemSystemWedge.getContentClientFromTarget(reference.getTarget());
			
			contentClient.contentFailed("Cannot add reference to Meem in LifeCycleState of "
					+ lastTransition.getCurrentState().getCurrentState());
		}
	}

	private synchronized void doRemoveOutboundReference(Meem meem, Reference reference) {
		if (reference.getFacetIdentifier().equals("lifeCycleClient")) {
			removeLifeCycleReference(reference);
		} else if (meem != null) {
			meem.removeOutboundReference(reference);
		}
	}
	
	private synchronized void doAddDependency(Meem meem, String facetId, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		meem.addDependency(facetId, dependencyAttribute, lifeTime);
	}
	
	private synchronized void doAddDependency(Meem meem, Facet facet, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		meem.addDependency(facet, dependencyAttribute, lifeTime);
	}
	
	private synchronized void doRemoveDependency(Meem meem, DependencyAttribute dependencyAttribute) {
		meem.removeDependency(dependencyAttribute);
	}
	
	private synchronized void doUpdateDependency(Meem meem, DependencyAttribute dependencyAttribute) {
		meem.updateDependency(dependencyAttribute);
	}
	
	private void addLifeCycleReference(Reference reference, boolean automaticRemove) {
		Facet target = reference.getTarget();
		ContentClient contentClient = MeemSystemWedge.getContentClientFromTarget(target);
		
		if (target instanceof LifeCycleClient) {
			if (reference.isContentRequired()) {
				LifeCycleClient lifeCycleClient = (LifeCycleClient) target;
				lifeCycleClient.lifeCycleStateChanged(lastTransition);
			}

			contentClient.contentSent();

			if (!automaticRemove) {
				getLifeCycleClientRefs().put(reference, (RequestStack) RequestTracker.getRequestStack().clone());
			}
		} else {
			contentClient.contentFailed("Target must be of type: " + LifeCycleClient.class);
		}
		
		getClient().startResolving();  // this needs to be done
	}
	
	private void removeLifeCycleReference(Reference reference) {
		getLifeCycleClientRefs().remove(reference);
		getClient().startResolving();  // this needs to be done
	}

	private synchronized Map<Reference, RequestStack> getLifeCycleClientRefs() {
		if (lifeCycleClientRefs == null) {
			lifeCycleClientRefs = new HashMap<Reference, RequestStack>();
		}

		return lifeCycleClientRefs;
	}

	private synchronized void notifyLastTransition(LifeCycleTransition transition) {
		Iterator iterator = getLifeCycleClientRefs().entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Reference reference = (Reference) entry.getKey();
			RequestStack currentRequestStack = RequestTracker.getRequestStack();
			RequestStack requestStack = (RequestStack) entry.getValue();
			//	if (requestStack != null && !lifeCycleContentSent) {
			RequestTracker.setRequestStack(requestStack);
			//	}

			LifeCycleClient client = (LifeCycleClient) reference.getTarget();

			client.lifeCycleStateChanging(transition);
			client.lifeCycleStateChanged(transition);

			//	if (requestStack != null && !lifeCycleContentSent) {
			RequestTracker.setRequestStack(currentRequestStack);
			//	}
		}
	}
	
	/* ------------------------- inner classes ---------------------------- */

	/**
	 * Used to queue invocations and resolve unbound Meem
	 */
	private final class Client implements InvocationHandler {
		private final MeemUnbound parent;
		private List<MethodInvocation> queue = new LinkedList<MethodInvocation>();
		private Object resolver = null;
		boolean done = false;

		Client(MeemUnbound parent) {
			this.parent = parent;
		}

		public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (isDone()) {
				logger.log(Level.INFO, "Can not queue invocations. Proxy has resolved Meem");
				return null;
			}

			MethodInvocation invocation = new MethodInvocation(method, args);
			queue.add(invocation);
			startResolving();	// make sure we have started to resolve the meem
			return null;
		}
		
		public synchronized boolean isDone() {			
			return done;
		}

		private synchronized void startResolving() {
			if (resolver == null) {
				if (parent.meemPath.isDefinitive()) {
					resolver = new MeemUnbound.RegistryClient(this, parent.meemPath);
				} 
				else {
					resolver = new MeemUnbound.ResolverClient(this, parent.meemPath);
				}
			}
		}
		
		protected synchronized void resetResolver() {
			resolver = null;
		}

		/**
		 * Meem has been resolved, so process queue on meem
		 */
		public synchronized void flush() {
			done = true;
			resolver = null;

			RequestStack currentRequestStack = RequestTracker.getRequestStack();
			try {
				while (queue.size() > 0) {
					MethodInvocation invocation = (MethodInvocation) queue.remove(0);
					try {
						invocation.method.invoke(MeemUnbound.this, invocation.args);
					}
					catch (InvocationTargetException e) {
						logger.log(Level.INFO, "Problem invoking meem method", e);
					}
					catch (IllegalAccessException e) {
						logger.log(Level.INFO, "Problem invoking meem method", e);
					}
				}
			}
			catch (IndexOutOfBoundsException e) {
				// end of queue
			}
			RequestTracker.setRequestStack(currentRequestStack);

		}
		
		private boolean compareRemote(Meem boundMeem) {
			if (Proxy.isProxyClass(this.parent.meem.getClass())) {
				InvocationHandler ih = Proxy.getInvocationHandler(this.parent.meem);
				if (ih instanceof SmartProxyMeem) {
					InvocationHandler ih2 = Proxy.getInvocationHandler(boundMeem);
					return !(ih2 instanceof SmartProxyMeem);
				}
			}
			return false;
		}
		
		/**
		 * Called when the meem is bound.
		 * 
		 * @param boundMeem
		 */
		synchronized void bind(Meem boundMeem) {
			synchronized (this.parent) {

				if (this.parent.meem == null || boundMeem == null || !this.parent.meem.equals(boundMeem) || compareRemote(boundMeem)) {
					if (this.parent.meem != null) {
						lifeCycleClientImpl.dispose();
						lifeCycleClientImpl = null;

						LifeCycleState currentState = lastTransition.getCurrentState();

						if (!currentState.equals(LifeCycleState.ABSENT)) {
							if (!currentState.equals(LifeCycleState.DORMANT)) {
								if (!currentState.equals(LifeCycleState.LOADED)) {
									if (!currentState.equals(LifeCycleState.PENDING)) {
										notifyLastTransition(LifeCycleTransition.READY_PENDING);
									}

									notifyLastTransition(LifeCycleTransition.PENDING_LOADED);
								}

								notifyLastTransition(LifeCycleTransition.LOADED_DORMANT);
							}

							notifyLastTransition(LifeCycleTransition.DORMANT_ABSENT);

							lastTransition = LifeCycleTransition.DORMANT_ABSENT;
						}
					}

					this.parent.meem = boundMeem;

					if (boundMeem != null) {
						lifeCycleClientImpl = new LifeCycleClientImpl(this.parent);
					}
				}
			}
		}
	}
	
	/**
	 * Listens on lifecycle state of the bound meem
	 */
	private final class LifeCycleClientImpl implements LifeCycleClient, ContentClient {
		private final MeemUnbound parent;
		private boolean disposed = false;

		public LifeCycleClientImpl(MeemUnbound parent) {
			this.parent = parent;

			// listen for lifecycle state
			LifeCycleClient lifeCycleClient = (LifeCycleClient) GatewayManagerWedge.getTargetFor(this, LifeCycleClient.class);
			Reference reference = Reference.spi.create("lifeCycleClient", lifeCycleClient, true);
			meem.addOutboundReference(reference, false);
		}

		public void dispose() {
			disposed = true;
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (!disposed) {
				LifeCycleState actualCurrentState = lastTransition.getCurrentState();
				if (!transition.getCurrentState().equals(actualCurrentState)) {
					LifeCycleState expectedCurrentState = transition.getPreviousState();

					synchronized (parent) {
						if (!expectedCurrentState.equals(actualCurrentState)) {
							int expectedIndex = LifeCycleState.STATES.indexOf(expectedCurrentState);
							int actualIndex = LifeCycleState.STATES.indexOf(actualCurrentState);

							int increment = actualIndex < expectedIndex ? 1 : -1;

							do {
								int nextIndex = actualIndex + increment;

								LifeCycleTransition nextTransition = new LifeCycleTransition(
										(LifeCycleState) LifeCycleState.STATES.get(actualIndex), 
										(LifeCycleState) LifeCycleState.STATES.get(nextIndex)
									);

								notifyLastTransition(nextTransition);

								actualIndex = nextIndex;
							} while (actualIndex != expectedIndex);
						}

						notifyLastTransition(transition);

						lastTransition = transition;
					}
				}
			}
		}

		public void contentSent() {
			parent.client.flush();
		}

		public void contentFailed(String reason) {
		}
	};
	
	/**
	 * 
	 */
	private final class RegistryClient implements MeemRegistryClient, ContentClient {
		private final Client parent;
		private final MeemPath meemPath;
		private final Reference reference;
		private final MeemRegistryClient proxy;
		private boolean hasContentSent = false;
		private Meem meem = null;

		RegistryClient(Client parent, MeemPath meemPath) {
			this.parent = parent;
			this.meemPath = meemPath;

			this.proxy = (MeemRegistryClient) GatewayManagerWedge.getTargetFor(this, MeemRegistryClient.class);

			reference = Reference.spi.create("meemRegistryClient", proxy, true, new ExactMatchFilter(meemPath));

			Meem registryMeem = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());

			registryMeem.addOutboundReference(reference, false);
		}

		public void meemRegistered(Meem meem) {
			if (hasContentSent) {
				this.parent.bind(meem);
			} else {
				this.meem = meem;
			}
		}

		/**
		 * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemDeregistered(org.openmaji.meem.Meem)
		 */
		public void meemDeregistered(Meem meem) {
			if (hasContentSent) {
				parent.bind(null);
			} else {
				this.meem = null;
			}
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent() {
			this.parent.bind(this.meem);

			hasContentSent = true;
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
		 */
		public void contentFailed(String reason) {
			// TODO [dgh] this needs to be handled properly
			System.err.println("meem unbound content fail: " + meemPath + " : " + reason);
			getClient().resetResolver();	// tell client it has not yet started
			this.parent.bind(null);
		}
	}

	/**
	 * Used to resolve the Meem given by MeemPath
	 */
	private final class ResolverClient implements MeemResolverClient, ContentClient {
		private Client parent;

		private final Reference reference;

		private boolean hasContentSent = false;

		private Meem meem = null;

		ResolverClient(Client parent, MeemPath meemPath) {
			this.parent = parent;

			Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());
			MeemResolverClient proxy = (MeemResolverClient) GatewayManagerWedge.getTargetFor(this, MeemResolverClient.class);
			Filter filter = new ExactMatchFilter(meemPath);

			this.reference = Reference.spi.create("meemResolverClient", proxy, true, filter);

			resolverMeem.addOutboundReference(reference, false);
		}

		/**
		 * @see org.openmaji.system.space.resolver.MeemResolverClient#meemResolved(org.openmaji.meem.MeemPath,
		 *      org.openmaji.meem.Meem)
		 */
		public void meemResolved(MeemPath meemPath, Meem meem) {
			if (hasContentSent) {
				this.parent.bind(meem);
			}
			else {
				this.meem = meem;
			}
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent() {
			this.parent.bind(this.meem);
			hasContentSent = true;
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
		 */
		public void contentFailed(String reason) {
			// TODO [dgh] this needs to be handled properly
			getClient().resetResolver();	// tell client it has not yet started
			this.parent.bind(null);
		}
	}
	
	/**
	 * Represents a method invocation
	 */
	private class MethodInvocation {
		private final Method method;
		private final Object[] args;
		MethodInvocation(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}
	}
}