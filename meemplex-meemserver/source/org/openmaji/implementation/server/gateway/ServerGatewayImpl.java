/*
 * @(#)ServerGatewayImpl.java
 * 
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.gateway;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;

import javax.security.auth.Subject;

import org.openmaji.implementation.server.genesis.ShutdownHelper;
import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitLifeCycleManager;
import org.openmaji.implementation.server.manager.user.UserManagerMeem;
import org.openmaji.implementation.server.security.DoAsMeem;
import org.openmaji.implementation.server.security.auth.AuthenticatorLookup;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.FacetDescriptor;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.server.helper.LifeCycleManagerHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.gateway.FacetConsumer;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import org.openmaji.system.manager.registry.MeemRegistry;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meemkit.core.MeemkitManager;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.meemserver.controller.MeemServerController;
import org.openmaji.system.space.hyperspace.HyperSpace;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.space.resolver.MeemResolver;

/**
 * TODO allow clients to get Targets of Meems whose calls are wrapped in the Subject associated with this SeverGateway e.g. Facet getTarget(final Meem meem, final String
 * facetIdentifier, final Class specification)
 */
public class ServerGatewayImpl implements ServerGateway {
	private Subject subject;

	private static Hashtable<String, String> lookUp;

	private static String essentialPath;

	static {
		// hack used to locate essential Meems

		lookUp = new Hashtable<String, String>();

		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/persistingLifeCycleManager", EssentialLifeCycleManager.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/transientLifeCycleManager", "_TRANSIENT_LCM");
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemStore", MeemStore.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemServerController", MeemServerController.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/userManager", UserManagerMeem.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemRegistry", MeemRegistry.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/hyperSpace", HyperSpace.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemResolver", MeemResolver.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitManager", MeemkitManager.spi.getIdentifier());
		// lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/licenseStoreFactory", LicenseStoreFactoryMeem.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitLifeCycleManager", MeemkitLifeCycleManager.spi.getIdentifier());
		lookUp.put(MeemServer.spi.getEssentialMeemsCategoryLocation() + "/authenticatorLookup", AuthenticatorLookup.spi.getIdentifier());

		essentialPath = MeemServer.spi.getEssentialMeemsCategoryLocation();
	}

	/**
	 * Default constructor - use the current subject.
	 */
	public ServerGatewayImpl() {
		this(Subject.getSubject(java.security.AccessController.getContext()));
	}

	/**
	 * Create a gateway for the passed in subject.
	 * 
	 * @param subject
	 *            the current access credentials.
	 */
	public ServerGatewayImpl(Subject subject) {
		if (!MeemCoreRootAuthority.isValidSubject(subject)) {
			throw new IllegalArgumentException("invalid subject passed to ServerGateway constructor.");
		}

		this.subject = subject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.gateway.ServerGateway#getEssentialMeem(java.lang.String)
	 */
	public Meem getEssentialMeem(String meemIdentifier) {
		return new DoAsMeem(EssentialMeemHelper.getEssentialMeem(meemIdentifier), subject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.gateway.ServerGateway#getMeem(org.openmaji.meem.MeemPath)
	 */
	public Meem getMeem(MeemPath path) {
		if (path.getSpace().equals(Space.HYPERSPACE) && path.getLocation().startsWith(essentialPath)) {
			String meemIdentifier = (String) lookUp.get(path.getLocation());

			if (meemIdentifier.equals("_TRANSIENT_LCM")) {
				return new DoAsMeem(LifeCycleManagerHelper.getTransientLCM(), subject);
			}
			Meem meem = EssentialMeemHelper.getEssentialMeem(meemIdentifier);
			if (meem == null) {
				return null;
			}
			return new DoAsMeem(meem, subject);
		}
		return new DoAsMeem(Meem.spi.get(path), subject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.gateway.ServerGateway#getTargetFor(org.openmaji.meem.Facet, java.lang.Class)
	 */
	public <T extends Facet> T getTargetFor(final T facet, final Class<T> specification) {
		return ((T) Subject.doAs(subject, new PrivilegedAction<T>() {
			public T run() {
				return (T) GatewayManagerWedge.getTargetFor(facet, specification);
			}
		}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.gateway.ServerGateway#revokeTarget(org.openmaji.meem.Facet, org.openmaji.meem.Facet)
	 */
	public void revokeTarget(final Facet proxy, final Facet implementation) {
		Subject.doAs(subject, new PrivilegedAction<Void>() {
			public Void run() {
				GatewayManagerWedge.revokeTarget(proxy, implementation);

				return null;
			}
		});
	}

	/**
	 * Asynchronous method to get a facet target.
	 */
	public <T extends Facet> void getTarget(final Meem meem, final String facetIdentifier, final Class<T> specification, final AsyncCallback<T> callback) {
		Subject.doAs(subject, new PrivilegedAction<Void>() {
			public Void run() {
				Facet proxy = MeemClientImpl.getProxy(meem, new FacetConsumer<T>() {
					public void facet(Meem meem, String facetId, T target) {
						callback.result(target);
					};
				});
				Filter filter = new FacetDescriptor(facetIdentifier, specification);
				Reference targetReference = Reference.spi.create("meemClientFacet", proxy, true, filter);
				meem.addOutboundReference(targetReference, true);	// a one-off retrieval of content

				return null;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.gateway.ServerGateway#getTarget(Meem, String, Class)
	 */
	public <T extends Facet> T getTarget(final Meem meem, final String facetIdentifier, final Class<T> specification) {
		return Subject.doAs(subject, new PrivilegedAction<T>() {
			public T run() {
				T facet = (T) ReferenceHelper.getTarget(meem, facetIdentifier, specification);
				if (facet == null) {
					return null;
				}
				
				@SuppressWarnings("unchecked")
				T reference = (T) Proxy.newProxyInstance(specification.getClassLoader(), getClasses(facet, specification), new SubjectInvocationHandler(subject, facet));
				return reference;
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.gateway.ServerGateway#getTarget(Facet, Class)
	 */
	public <T extends Facet> T getTarget(final T facet, final Class<T> specification) {
		@SuppressWarnings("unchecked")
		T reference = (T) Proxy.newProxyInstance(specification.getClassLoader(), getClasses(facet, specification), new SubjectInvocationHandler(subject, facet));

		return reference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.gateway.ServerGateway#shutdown()
	 */
	public boolean shutdown() {
		return  Subject.doAs(subject, new PrivilegedAction<Boolean>() {
			public Boolean run() {
				return Boolean.valueOf(ShutdownHelper.shutdownMaji());
			}
		}).booleanValue();
	}

	private Class<?>[] getClasses(Facet facet, Class<?> specification) {
		Class<?>[] classes;
		int classesSize = 1;
		boolean isMeem = false;
		boolean isContentClient = false;

		if (facet instanceof Meem) {
			isMeem = true;
			classesSize++;
		}
		if (facet instanceof ContentClient) {
			isContentClient = true;
			classesSize++;
		}

		int upto = 0;
		classes = new Class[classesSize];
		classes[upto] = specification;
		if (isMeem) {
			classes[++upto] = Meem.class;
		}
		if (isContentClient) {
			classes[++upto] = ContentClient.class;
		}

		return classes;
	}

	/**
	 * Invokes methods on a Facet target in the context of the logged in user.
	 */
	private static final class SubjectInvocationHandler implements InvocationHandler, Serializable {
		private static final long serialVersionUID = 1948264783905L;

		private Object targetObject;

		private transient Subject subject = null;

		public SubjectInvocationHandler(Subject subject, Object object) {
			this.subject = subject;
			this.targetObject = object;
		}

		public Object invoke(final Object obj, final Method method, final Object[] args) throws Throwable {
			if (subject == null) {
				return method.invoke(targetObject, args);
			}
			else {
				PrivilegedExceptionAction<Object> action = new PrivilegedExceptionAction<Object>() {
					public Object run() throws Exception {
						return method.invoke(targetObject, args);
					}
				};
				return Subject.doAs(subject, action);
			}
		}
	};

	/**
	 * 
	 */
	private static final class MeemClientImpl <T extends Facet> implements MeemClient, ContentClient {
		private Meem meem;

		private FacetConsumer<T> consumer;

		private Facet proxy;

		public static <T extends Facet> Facet getProxy(Meem meem, FacetConsumer<T> consumer) {
			MeemClientImpl<T> meemClient = new MeemClientImpl<T>(meem, consumer);
			return meemClient.getProxy();
		}

		private MeemClientImpl(Meem meem, FacetConsumer<T> consumer) {
			this.meem = meem;
			this.consumer = consumer;
		}

		public void referenceAdded(Reference reference) {
			T facet = reference.getTarget();
			consumer.facet(meem, reference.getFacetIdentifier(), facet);
		}

		public void referenceRemoved(Reference reference) {
			consumer.facet(meem, reference.getFacetIdentifier(), null);
		}

		public void contentSent() {
			if (consumer instanceof ContentClient) {
				((ContentClient) consumer).contentSent();
			}
			revokeProxy();
		}

		public void contentFailed(String reason) {
			if (consumer instanceof ContentClient) {
				((ContentClient) consumer).contentFailed(reason);
			}
			revokeProxy();
		}

		private Facet getProxy() {
			if (proxy == null) {
				this.proxy = GatewayManagerWedge.getTargetFor(this, MeemClient.class);
			}
			return proxy;
		}

		private void revokeProxy() {
			GatewayManagerWedge.revokeTarget(proxy, this);
			proxy = null;
		}
	}

}
