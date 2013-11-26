/*
 * @(#)MeemStoreProxyWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.openmaji.implementation.server.meem.wedge.lifecycle.SystemLifeCycleClientAdapter;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.meem.*;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.filter.*;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.space.meemstore.MeemStoreClient;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class MeemStoreProxyWedge implements MeemStore, MeemStoreProxy, MeemStoreClientProxy, MeemDefinitionClientProxy, MeemContentClientProxy, Wedge, FilterChecker {

	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemStoreClient meemStoreClient;
	public MeemContentClient meemContentClient;
	public MeemDefinitionClient meemDefinitionClient;

	private Map<Reference<?>, Reference<?>> references = new HashMap<Reference<?>, Reference<?>>();

	private Meem meemStoreMeem = null;
	private MeemStore meemStore = null;
	private Object meemStoreQueueProxy = createMeemStoreProxy();
	private static MeemStoreProxyWedge instance = null;

	public MeemContext meemContext;
	private Boolean gotMeemStore = Boolean.FALSE;

	public Vote lifeCycleControlConduit;

	public MeemClient meemReferenceClientConduit = new MeemClientImpl();
	public MeemClientConduit meemClientConduit;

	public LifeCycleClient lifeCycleClientConduit = new SystemLifeCycleClientAdapter(this);

	public MeemStoreProxyWedge() {
		instance = this;
	}

	public void conclude() {
		for (Reference<?> ref : references.values()) {
			getMeemStoreMeem().removeOutboundReference(ref);
		}
	}

	public static void setMeemStore(Meem meemStoreMeem) {
		instance.setMeemStoreMeem(meemStoreMeem);
	}

	public void setMeemStoreMeem(Meem meemStoreMeem) {
		if (meemStoreMeem == null && gotMeemStore.equals(Boolean.TRUE)) {
			synchronized (gotMeemStore) {
				gotMeemStore = Boolean.FALSE;
				this.meemStoreMeem = null;
				meemStore = null;
				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			}
		}
		else if (meemStoreMeem != null && gotMeemStore.equals(Boolean.FALSE)) {
			logger.log(Level.INFO, "Setting MeemStore: " + meemStoreMeem);

			this.meemStoreMeem = meemStoreMeem;

			MeemClient proxy = meemContext.getTargetFor(new MeemClientCallbackImpl(), MeemClient.class);
			Filter filter = new FacetDescriptor("meemStore", MeemStore.class);
			Reference<MeemClient> targetReference = Reference.spi.create("meemClientFacet", proxy, true, filter);

			meemStoreMeem.addOutboundReference(targetReference, true);
		}
		else {
			logger.log(Level.WARNING, "MeemStore already found. Stale remote system Meems may not have expired in Jini Lookup Service.");
		}
	}

	public static class MeemClientCallbackImpl implements MeemClient {

		public void referenceAdded(Reference<?> reference) {
			instance.meemStore = (MeemStore) reference.getTarget();

			logger.log(Level.INFO, "MeemStore set: " + instance.meemStore);
			synchronized (instance.gotMeemStore) {
				instance.gotMeemStore = Boolean.TRUE;
				((MeemStoreQueueProxy) Proxy.getInvocationHandler(instance.meemStoreQueueProxy)).runQueue();
				instance.lifeCycleControlConduit.vote(instance.meemContext.getWedgeIdentifier(), true);
			}
		}

		public void referenceRemoved(Reference<?> reference) {
		}

	}

	private MeemStore getMeemStore() {
		return (MeemStore) meemStoreQueueProxy;
	}

	private Meem getMeemStoreMeem() {
		return (Meem) meemStoreQueueProxy;
	}

	public final ContentProvider<MeemStoreClient> meemStoreClientProvider = new ContentProvider<MeemStoreClient>() {
		public void sendContent(MeemStoreClient client, Filter filter) throws IllegalArgumentException {

			Reference<MeemStoreClient> newReference = Reference.spi.create("meemStoreClient", client, true, filter);
			getMeemStoreMeem().addOutboundReference(newReference, true);
		}
	};

	public final AsyncContentProvider<MeemContentClient> meemContentClientProvider = new AsyncContentProvider<MeemContentClient>() {
		public void asyncSendContent(MeemContentClient target, Filter filter, ContentClient contentClient) {
			new MeemContentSendContent(target, filter, contentClient);
		}
	};

	public class MeemContentSendContent implements MeemContentClient, ContentClient {
		private final ContentClient contentClient;
		private final MeemContentClient meemContentClient;

		public MeemContentSendContent(MeemContentClient meemContentClient, Filter filter, ContentClient contentClient) {
			this.contentClient = contentClient;
			this.meemContentClient = meemContentClient;

			MeemContentClient proxy = (MeemContentClient) meemContext.getLimitedTargetFor(this, MeemContentClient.class);

			Reference<MeemContentClient> newReference = Reference.spi.create("meemContentClient", proxy, true, filter);
			getMeemStoreMeem().addOutboundReference(newReference, true);
		}

		/**
		 * @see org.openmaji.system.space.meemstore.MeemContentClient#meemContentChanged(org.openmaji.meem.MeemPath,
		 *      org.openmaji.system.meem.definition.MeemContent)
		 */
		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			meemContentClient.meemContentChanged(meemPath, meemContent);
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent() {
			contentClient.contentSent();
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
		 */
		public void contentFailed(String reason) {
			contentClient.contentFailed(reason);
		}
	}

	public final AsyncContentProvider<MeemDefinitionClient> meemDefinitionClientProvider = new AsyncContentProvider<MeemDefinitionClient>() {
		public void asyncSendContent(MeemDefinitionClient target, Filter filter, ContentClient contentClient) {
			new MeemDefinitionSendContent(target, filter, contentClient);
		}
	};

	public class MeemDefinitionSendContent implements MeemDefinitionClient, ContentClient {
		private final ContentClient contentClient;
		private final MeemDefinitionClient meemDefinitionClient;

		public MeemDefinitionSendContent(MeemDefinitionClient meemDefinitionClient, Filter filter, ContentClient contentClient) {
			this.contentClient = contentClient;
			this.meemDefinitionClient = meemDefinitionClient;

			MeemDefinitionClient proxy = (MeemDefinitionClient) meemContext.getLimitedTargetFor(this, MeemDefinitionClient.class);

			Reference<MeemDefinitionClient> newReference = Reference.spi.create("meemDefinitionClient", proxy, true, filter);
			getMeemStoreMeem().addOutboundReference(newReference, true);
		}

		/**
		 * @see org.openmaji.system.space.meemstore.MeemDefinitionClient#meemDefinitionChanged(org.openmaji.meem.MeemPath,
		 *      org.openmaji.meem.definition.MeemDefinition)
		 */
		public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition) {
			meemDefinitionClient.meemDefinitionChanged(meemPath, meemDefinition);
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent() {
			contentClient.contentSent();
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
		 */
		public void contentFailed(String reason) {
			contentClient.contentFailed(reason);
		}

	}

	/* ------------------ MeemStore methods ---------- */

	/**
	 * @see org.openmaji.system.space.meemstore.MeemStore#destroyMeem(org.openmaji.meem.MeemPath)
	 */
	public void destroyMeem(MeemPath meemPath) {
		getMeemStore().destroyMeem(meemPath);
	}

	/**
	 * @see org.openmaji.system.space.meemstore.MeemStore#storeMeemContent(org.openmaji.meem.MeemPath,
	 *      org.openmaji.system.meem.definition.MeemContent)
	 */
	public void storeMeemContent(MeemPath meemPath, MeemContent meemContent) {
		getMeemStore().storeMeemContent(meemPath, meemContent);
	}

	/**
	 * @see org.openmaji.system.space.meemstore.MeemStore#storeMeemDefinition(org.openmaji.meem.MeemPath,
	 *      org.openmaji.meem.definition.MeemDefinition)
	 */
	public void storeMeemDefinition(MeemPath meemPath, MeemDefinition meemDefinition) {
		getMeemStore().storeMeemDefinition(meemPath, meemDefinition);
	}

	/* ------------------ MeemStoreClient methods ---------- */

	/**
	 * @see org.openmaji.system.space.meemstore.MeemStoreClient#meemDestroyed(org.openmaji.meem.MeemPath)
	 */
	public void meemDestroyed(MeemPath meemPath) {
		meemStoreClient.meemDestroyed(meemPath);
	}

	/**
	 * @see org.openmaji.system.space.meemstore.MeemStoreClient#meemStored(org.openmaji.meem.MeemPath)
	 */
	public void meemStored(MeemPath meemPath) {
		meemStoreClient.meemStored(meemPath);
	}

	/* --------------------- MeemDefinitionClient methods ---------------- */

	/**
	 * @see org.openmaji.system.space.meemstore.MeemDefinitionClient#meemDefinitionChanged(org.openmaji.meem.MeemPath,
	 *      org.openmaji.meem.definition.MeemDefinition)
	 */
	public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition) {
		meemDefinitionClient.meemDefinitionChanged(meemPath, meemDefinition);
	}

	/* --------------------- MeemContentClient methods ---------------- */

	/**
	 * @see org.openmaji.system.space.meemstore.MeemContentClient#meemContentChanged(org.openmaji.meem.MeemPath,
	 *      org.openmaji.system.meem.definition.MeemContent)
	 */
	public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
		meemContentClient.meemContentChanged(meemPath, meemContent);
	}

	/* --------------------- FilterChecker methods ---------------- */

	/**
	 * @see org.openmaji.meem.filter.FilterChecker#invokeMethodCheck(org.openmaji.meem.filter.Filter,
	 *      java.lang.String, java.lang.Object[])
	 */
	public boolean invokeMethodCheck(Filter filter, String facetName, String methodName, Object[] args) throws IllegalFilterException {
		if (!(filter instanceof ExactMatchFilter)) {
			throw new IllegalFilterException("Can't check filter: " + filter);
		}

		if (methodName.equals("meemDefinitionChanged") || methodName.equals("meemContentChanged")) {

			MeemPath meemPath = (MeemPath) args[0];
			if (meemPath != null) {
				ExactMatchFilter<?> exactMatchFilter = (ExactMatchFilter<?>) filter;
				return exactMatchFilter.getTemplate().equals(meemPath);
			}
		}

		return false;
	}

	private Object createMeemStoreProxy() {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { Meem.class, MeemStore.class }, new MeemStoreQueueProxy());
	}

	class MeemStoreQueueProxy implements InvocationHandler {

		private class Invocation {
			private final String methodName;
			private final Object[] args;

			public Invocation(String methodName, Object[] args) {
				this.methodName = methodName;
				this.args = args;
			}

			public Object[] getArgs() {
				return args;
			}

			public String getMethodName() {
				return methodName;
			}
		}

		private LinkedList<Invocation> queue = new LinkedList<Invocation>();

		public void runQueue() {
			synchronized (queue) {
				while (queue.size() > 0) {
					Invocation inv = (Invocation) queue.remove(0);
					Object[] args = inv.getArgs();

					if (inv.getMethodName().equals("destroyMeem")) {
						meemStore.destroyMeem((MeemPath) args[0]);
					}
					if (inv.getMethodName().equals("storeMeemContent")) {
						meemStore.storeMeemContent((MeemPath) args[0], (MeemContent) args[1]);
					}
					if (inv.getMethodName().equals("storeMeemDefinition")) {
						meemStore.storeMeemDefinition((MeemPath) args[0], (MeemDefinition) args[1]);
					}
					if (inv.getMethodName().equals("addOutboundReference")) {
						meemStoreMeem.addOutboundReference((Reference<?>) args[0], ((Boolean) args[1]).booleanValue());
					}
					if (inv.getMethodName().equals("removeOutboundReference")) {
						meemStoreMeem.removeOutboundReference((Reference<?>) args[0]);
					}
				}
			}
		}

		/**
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			synchronized (queue) {
				queue.addLast(new Invocation(method.getName(), args));
			}
			synchronized (gotMeemStore) {
				if (gotMeemStore.equals(Boolean.TRUE)) {
					runQueue();
				}
			}
			return null;
		}
	}

	class MeemClientImpl implements MeemClient {

		/**
		 * @see org.openmaji.meem.MeemClient#referenceAdded(org.openmaji.meem.wedge.reference.Reference)
		 */
		public void referenceAdded(Reference<?> reference) {
			if (reference.getFacetIdentifier().equals("meemStoreClient")) {
				Reference<?> newReference = Reference.spi.create(reference.getFacetIdentifier(), meemContext.getTarget("meemStoreClientProxy"), false, reference.getFilter());
				getMeemStoreMeem().addOutboundReference(newReference, false);
				references.put(reference, newReference);
			}
			if (reference.getFacetIdentifier().equals("meemContentClient")) {
				Reference<?> newReference = Reference.spi.create(reference.getFacetIdentifier(), meemContext.getTarget("meemContentClientProxy"), false, reference.getFilter());
				getMeemStoreMeem().addOutboundReference(newReference, false);
				references.put(reference, newReference);
			}
			if (reference.getFacetIdentifier().equals("meemDefinitionClient")) {
				Reference<?> newReference = Reference.spi.create(reference.getFacetIdentifier(), meemContext.getTarget("meemDefinitionClientProxy"), false, reference.getFilter());
				getMeemStoreMeem().addOutboundReference(newReference, false);
				references.put(reference, newReference);
			}
		}

		/**
		 * @see org.openmaji.meem.MeemClient#referenceRemoved(org.openmaji.meem.wedge.reference.Reference)
		 */
		public void referenceRemoved(Reference<?> reference) {
			if (reference.getFacetIdentifier().equals("meemStoreClient") || reference.getFacetIdentifier().equals("meemContentClient") || reference.getFacetIdentifier().equals("meemDefinitionClient")) {
				Reference<?> newReference = references.get(reference);
				getMeemStoreMeem().removeOutboundReference(newReference);
			}
		}

	}

}
