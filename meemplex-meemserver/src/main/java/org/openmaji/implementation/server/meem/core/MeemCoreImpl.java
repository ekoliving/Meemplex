/*
 * @(#)MeemCoreImpl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Ensure thread safety.
 * - Complete Dependency run-time model creation.
 * - System Wedges can only be added when Meem is not loaded.
 * - Application Definitions can be altered only when Meem is loaded.
 * - Consider circumstances under which adding Facets to a system Wedge
 *     could be a security violation.
 *
 * - Significant Design Consideration:
 *   Refactor InvocationProcessor, InvocationContext and InvocationList, etc.
 *   Once everything has settled down.
 *
 * - Significant Design Consideration:
 *   It should be possible to design a set of Meem and system Wedge interfaces
 *   such that the Java Dynamic Proxy Object isn't required for smaller
 *   implementation footprints.  For example, the Meem Facet should
 *   provide the add/removeReference() methods.  Whether a given implementation
 *   uses the Java Dynamic Proxy Object for a more effective implemention
 *   should be a option.  Another consider, the Dynamic Proxy should exist
 *   for all in-bound Facets that are of a unique type, the data structures
 *   can be manipulated when the Meem LifeCycleState is LOADED and the
 *   Java Dynamic Proxy Object is only rebuilt as the Meem becomes PENDING_READY.
 *   Only in-bound Facets that are of the same type would require their own
 *   Java Dynamic Proxy Object (one for two multiple type Facets, another for
 *   three and so on).
 */

package org.openmaji.implementation.server.meem.core;

import java.io.Serializable;
import java.lang.reflect.*;
import java.security.Principal;
import java.util.*;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

import org.openmaji.implementation.server.classloader.MajiClassLoader;
import org.openmaji.implementation.server.classloader.SystemExportList;
import org.openmaji.implementation.server.meem.*;
import org.openmaji.implementation.server.meem.definition.MeemDefinitionStructureAdapter;
import org.openmaji.implementation.server.meem.invocation.*;
import org.openmaji.implementation.server.meemserver.MeemServerWedge;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.implementation.server.utility.*;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.hook.invoke.InvocationListProvider;
import org.openmaji.system.meem.hook.security.AccessControl;
import org.openmaji.system.meem.hook.security.AccessLevel;
import org.openmaji.system.meem.hook.security.Principals;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.hyperspace.HyperSpace;
import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationType;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.MeemDefinition
 */

public class MeemCoreImpl implements MeemBuilder, MeemCore {
	static private MeemDefinition systemMeemDefinition = null;

	// static private Class[] systemInboundFacets = null;

	static private long facetCounter = 1;

	private MeemCoreStructure meemCoreStructure = null;

	private InvocationListProviderImpl invocationListProvider = null;

	/**
	 * Collection of Conduits
	 */

	private Hashtable<String, Conduit<?>> conduits = new Hashtable<String, Conduit<?>>();

	/**
	 * Collection of inbound facet proxies
	 */
	private Map<String, Facet> facetProxies = new HashMap<String, Facet>();

	/**
	 * Reference to the local FlightRecorder Meem
	 */

	private Meem flightRecorder = null;

	/**
	 * Reference to the parent LifeCycleManager Meem
	 */

	private Meem lifeCycleManager = null;

	/**
	 * Reference to the local MeemRegistry Meem
	 */

	private Meem meemRegistry = null;

	/**
	 * Reference to the local ThreadManager Meem
	 */

	private Meem threadManagerMeem = null;

	/**
	 * reference to the local security manager meem
	 */
	private Meem securityManager = null;

	/**
	 * Reference to the local MeemStore Meem
	 */

	private Meem meemStore = null;

	/**
	 * Unique MeemPath that allows it to be distinguished from every other Meem.
	 */

	protected final MeemPath meemPath;

	/**
	 * Provide the MeemPath that refers to a location within a Space that is specifically used for "storage". Each Meem's MeemPath should be unique.
	 * 
	 * @return Unique MeemPath for this Meem
	 */
	public MeemPath getMeemPath() {
		return (meemPath);
	}

	private static MajiSystemProvider majiSystemProvider = null;

	public static MeemCore getInstance(MeemPathImpl meemPath) {
		return new MeemCoreImpl(meemPath);
	}

	private MeemCoreImpl(MeemPath meemPath) {

		if (meemPath == null) {
			throw new RuntimeException("meemPath cannot be null");
		}

		this.meemPath = meemPath;

		if (systemMeemDefinition == null) {
			if (majiSystemProvider == null) {
				majiSystemProvider = MajiSystemProvider.systemProvider();
			}

			Collection<Class<?>> specifications = new ArrayList<Class<?>>();
			specifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_WEDGE));
			specifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_HOOK));

			systemMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(specifications);

			markSystemWedges(systemMeemDefinition);

			/*
			 * systemInboundFacets = MeemUtility.getMeemInterfaces( systemMeemDefinition, Direction.INBOUND);
			 */
		}

		meemCoreStructure = new MeemCoreStructure(this, MeemStructure.spi.create());

		addMeemDefinition(systemMeemDefinition, null, true);

		invocationListProvider = new InvocationListProviderImpl(this, InvocationListIdentifierProvider.spi.create());

		addConduitTarget("threadManager", ThreadManager.class, blockingDip);

	}

	/* ---------- MeemBuilder method(s) ------------------------------------------- */

	public void addMeemDefinition(MeemDefinition meemDefinition) {

		addMeemDefinition(meemDefinition, null, false);
	}

	public void addMeemDefinition(MeemDefinition meemDefinition, Object existingWedgeImplementation) {

		addMeemDefinition(meemDefinition, existingWedgeImplementation, false);
	}

	private void addMeemDefinition(MeemDefinition meemDefinition, Object existingWedgeImplementation, boolean systemWedgeFlag) {

		MeemDefinitionStructureAdapter.mergeMeemDefinition(meemDefinition, meemCoreStructure, existingWedgeImplementation, systemWedgeFlag);

		if (this.isA(HyperSpace.class)) {
			AccessControl source = (AccessControl) getConduitSource("accessControl", AccessControl.class);

			if (MeemCoreRootAuthority.isSystemGroup(this.getCurrentSubject())) {
				source.addAccess((Principal) MeemCoreRootAuthority.getSubject().getPrincipals(X500Principal.class).toArray()[0], AccessLevel.ADMINISTER);
				source.addAccess(Principals.SYSTEM, AccessLevel.ADMINISTER);
			}
			else {
				throw new IllegalStateException("hyperSpace cannot be created by other than the system group.");
			}
		}

		if (this.isA(MeemServerWedge.class) || this.isA(MeemServer.class))
		// || this.isA(SubsystemManagerWedge.class))
		{
			AccessControl source = (AccessControl) getConduitSource("accessControl", AccessControl.class);

			source.addAccess(Principals.OTHER, AccessLevel.READ);
		}
	}

	public MeemCore getMeemCore() {
		return this;
	}

	/* ---------- MeemCore method(s) ---------------------------------------- */

	public <T> void addConduitTarget(String conduitIdentifier, Class<T> specification, T implementation) {

		Conduit<T> conduit = createConduit(conduitIdentifier, specification);

		conduit.addTarget(implementation);
	}

	private <T> Conduit<T> createConduit(String conduitIdentifier, Class<T> specification) {
		Conduit<T> conduit = (Conduit<T>) conduits.get(conduitIdentifier);

		if (conduit == null) {
			if (conduitIdentifier.equals("errorHandler")) {
				conduit = ConduitImpl.create(conduitIdentifier, specification);
			}
			else {
				conduit = ConduitImpl.create(conduitIdentifier, specification, (ErrorHandler) this.getConduitSource("errorHandler", ErrorHandler.class));
			}

			conduits.put(conduitIdentifier, conduit);
		}

		return (conduit);
	}

	public <T> T getConduitSource(String conduitIdentifier, Class<T> specification) {
		Conduit<T> conduit = createConduit(conduitIdentifier, specification);

		return (conduit.getProxy());
	}

	public <T> void removeConduitTarget(String conduitIdentifier, T implementation) {

		Conduit<T> conduit = (Conduit<T>) conduits.get(conduitIdentifier);

		if (conduit != null)
			conduit.removeTarget(implementation);
	}

	public void removeConduit(String conduitIdentifier) {
		conduits.remove(conduitIdentifier);
	}

	public Facet getTarget(String facetId) {
		Facet facet = null;
		synchronized (facetProxies) {
			facet = (Facet) facetProxies.get(facetId);

			if (!isValidProxy(facet)) {
				facet = null;
			}
			if (facet == null) {
				InboundFacetImpl<?> inboundFacetImpl = getInboundFacetImpl(facetId);
				if (inboundFacetImpl == null) {
					// must be a transient target
					facetProxies.remove(facetId);
				}
				else {
					facet = inboundFacetImpl.makeProxy();
					facetProxies.put(facetId, facet);
				}
			}
		}
		return facet;
	}

	public <T extends Facet> T  getTargetFor(T facet, Class<T> specification) {
		return getTargetProxy(facet, specification, false, false);
	}

	public <T extends Facet> T  proxyTargetFor(T facet, Class<T> specification) {
		return getTargetProxy(facet, specification, false, false);
	}

	public <T extends Facet> T  getLimitedTargetFor(T facet, Class<T> specification) {
		return getTargetProxy(facet, specification, true, false);
	}

	public <T extends Facet> T  getNonBlockingTargetFor(T facet, Class<T> specification) {
		return getTargetProxy(facet, specification, true, true);
	}

	private <T extends Facet> T  getTargetProxy(T facet, Class<T> specification, boolean limit, boolean nonBlocking) {
		if (!specification.isInstance(facet)) {
			throw new RuntimeException("Facet is not an instance of: " + specification);
		}

		ContentClient contentClient = (facet instanceof ContentClient) ? (ContentClient) facet : null;

		boolean isContentClient = (contentClient != null);
		boolean isMeem = Meem.class.isAssignableFrom(specification);

		int numInterfaces = 1;

		if (isContentClient) {
			++numInterfaces;
		}
		if (!isMeem && !limit) {
			++numInterfaces;
		}

		Class<?>[] interfaces = new Class[numInterfaces];

		int index = 0;

		if (!isMeem && !limit) {
			// TODO[peter] Don't export Meem on every proxy
			interfaces[index++] = Meem.class;
		}

		if (isContentClient) {
			interfaces[index++] = ContentClient.class;
		}

		interfaces[index++] = specification;

		String facetId = ":" + Long.toHexString(facetCounter++);// + " [" + specification.getName() + "] [" + facet.getClass().getName() + "]\n" + facet.toString();

		InvocationHandler invocationHandler = new MeemInvocationTarget(this, facetId, facet, contentClient, nonBlocking);

		ClassLoader classLoader;
		if (System.getProperty(MajiClassLoader.CLASSPATH_FILE) == null) {
			classLoader = this.getClass().getClassLoader();
		}
		else {
			classLoader = SystemExportList.getInstance().getClassLoaderFor(specification.getName());
		}

		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);

		synchronized (facetProxies) {
			facetProxies.put(facetId, proxy);
		}

		return proxy;
	}

	public void revokeTargetProxy(Facet proxy, Facet implementation) {
		if (proxy != null && implementation != null && Proxy.isProxyClass(proxy.getClass())) {
			InvocationHandler ih = Proxy.getInvocationHandler(proxy);

			if (ih != null && ih instanceof MeemInvocationTarget) {
				MeemInvocationTarget mit = (MeemInvocationTarget) ih;

				if (mit.revoke(implementation)) {
					synchronized (facetProxies) {
						facetProxies.remove(mit.getFacetIdentifier());
					}
				}
			}
		}
	}

	public void revokeAllProxies() {
		synchronized (facetProxies) {
			// System.err.println("Revoking " + facetProxies.size() + " proxies");

			// TODO[peter] Enabling this currently breaks things, but should be done!
			// Iterator iter = facetProxies.values().iterator();
			// while (iter.hasNext())
			// {
			// Facet proxy = (Facet) iter.next();
			// InvocationHandler ih = Proxy.getInvocationHandler(proxy);
			//
			// if (ih != null && ih instanceof MeemInvocationTarget)
			// {
			// MeemInvocationTarget mit = (MeemInvocationTarget) ih;
			//
			// mit.revoke();
			// }
			// }
			//
			// facetProxies.clear();
		}
	}

	public void revokeFacetProxies() {
		synchronized (facetProxies) {
			String[] facetIds = (String[]) facetProxies.keySet().toArray(new String[0]);
			for (int i = 0; i < facetIds.length; i++) {
				String facetId = facetIds[i];

				// if (getInboundFacetImpl(facetId) != null) {
				if (!facetId.startsWith(":")) {
					Facet proxy = (Facet) facetProxies.remove(facetId);

					// the following causes problems with things that maintain a reference to the facet proxies
					if (proxy != null) {
						// InvocationHandler ih = Proxy.getInvocationHandler(proxy);
						//
						// if (ih != null && ih instanceof MeemInvocationTarget) {
						// MeemInvocationTarget target = (MeemInvocationTarget) ih;
						// target.revoke();
						// }
					}
				}
			}
		}
	}

	public FacetImpl<?> getFacetImpl(String facetIdentifier, Direction direction) {
		if (direction.equals(Direction.INBOUND)) {
			return getInboundFacetImpl(facetIdentifier);
		}
		else {
			return getOutboundFacetImpl(facetIdentifier);
		}
	}

	public InboundFacetImpl<?> getInboundFacetImpl(String inboundFacetIdentifier) {
		return meemCoreStructure.getInboundFacetImpl(inboundFacetIdentifier);
	}

	public OutboundFacetImpl<?> getOutboundFacetImpl(String outboundFacetIdentifier) {
		return meemCoreStructure.getOutboundFacetImpl(outboundFacetIdentifier);
	}
	
	public Collection<FacetImpl<?>> getFacetImpls() {
		return meemCoreStructure.getFacetImpls();
	}

	public Meem getFlightRecorder() {
		return (flightRecorder);
	}

	public Meem getLifeCycleManager() {
		return (lifeCycleManager);
	}

	public Meem getIdentityManager() {
		return (securityManager);
	}

	/**
	 * Provide the value of an ImmutableAttribute.
	 * 
	 * @param key
	 *            Index for the required ImmutableAttribute value
	 * @return ImmutableAttribute value for the given key
	 * @exception IllegalArgumentException
	 *                ImmutableAttribute key isn't valid
	 */

	public Object getImmutableAttribute(Object key) throws IllegalArgumentException {

		return (meemCoreStructure.getMeemAttribute().getImmutableAttribute(key));
	}

	public Meem getMeemRegistry() {
		return (meemRegistry);
	}

	public MeemStructure getMeemStructure() {
		return (meemCoreStructure);
	}

	public Meem getSelf() { // This is sort of like "Being John Malkovich"
		return (Meem) getTarget("meem");
	}

	public Meem getThreadManager() {
		return (threadManagerMeem);
	}

	public Meem getMeemStore() {
		return (meemStore);
	}

	/**
	 * Note: This method does not guarentee that Wedge types are returned.
	 */

	public Collection<WedgeImpl> getWedgeImpls() {
		return (meemCoreStructure.getWedgeImpls());
	}

	/**
	 * <p>
	 * Cache references to local system Meems that are very useful.
	 * </p>
	 * Typically, this method should only be allowed to be invoked once. </p>
	 * 
	 * @param meemRegistry
	 *            Reference to the MeemRegistry Meem
	 * @param lifeCycleManager
	 *            Reference to the LifeCycleManager Meem
	 * @param securityManager
	 *            Reference to the SecurityManager Meem
	 * @param flightRecorder
	 *            Reference to the FlightRecorder Meem
	 * @exception RuntimeException
	 *                Already initialized
	 */

	public void initialize(Meem meemRegistry, Meem lifeCycleManager, Meem securityManager, Meem threadManagerMeem, Meem flightRecorder, Meem meemStore) throws RuntimeException {
		if (this.meemRegistry != null) {
			throw new RuntimeException("Meem already initialized: " + this.getMeemPath());
		}

		this.meemRegistry = meemRegistry;
		this.lifeCycleManager = lifeCycleManager;
		this.securityManager = securityManager;
		this.threadManagerMeem = threadManagerMeem;
		this.flightRecorder = flightRecorder;
		this.meemStore = meemStore;
	}

	public void setInvocationListIdentifierProvider(InvocationListIdentifierProvider listProvider) {
		this.invocationListProvider = new InvocationListProviderImpl(this, listProvider);
	}

	/**
     * 
     */
	public InvocationListProvider getInvocationListGenerator() {
		return invocationListProvider;
	}

	/**
	 * Return whether or not this MeemCore implements a given inbound facet.
	 * 
	 * @param specification
	 *            class to check for.
	 * @return boolean true if it does
	 */
	public boolean isA(Class<? extends Facet> specification) {
		for (WedgeImpl wedgeImpl : this.getWedgeImpls()) {
			for (InboundFacetImpl<?> facetImpl : wedgeImpl.getInboundFacets()) {
				if (facetImpl.getSpecification().equals(specification)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets all WedgeAttributes in the MeemDefinition to be marked as system wedges
	 * 
	 * @param meemDefinition
	 *            MeemDefinition to modify
	 */
	private void markSystemWedges(MeemDefinition meemDefinition) {
		for (WedgeDefinition wedgeDefinition : meemDefinition.getWedgeDefinitions()) {
			WedgeAttribute wedgeAttribute = wedgeDefinition.getWedgeAttribute();
			wedgeAttribute.setSystemWedge(true);
		}
	}

	public Meem getSystemImplementation() {
		return meemCoreStructure.getSystemImplementation();
	}

	public DecoupledInvocationProcessor getBlockingDip() {
		return blockingDip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.meem.core.MeemCore#getContent()
	 */
	public MeemContent getContent() {
		MeemContent meemContent = new MeemContent();

		synchronized (this) {
			for (WedgeImpl wedgeImpl : this.getWedgeImpls()) {
				wedgeImpl.parseWedge(meemContent);
			}
		}

		return meemContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.meem.core.MeemCore#loadContent(org.openmaji.system.meem.definition.MeemContent)
	 */
	public void restoreContent(MeemContent meemContent) {
		// public void restore(MeemContent meemContent) {
		// System.err.println("PersistenceHandlerWedge.restore");
		if (meemContent == null) {
			// System.err.println("PersistenceHandlerWedge.restore: meemContent == null");
			return;
		}

		Collection<WedgeImpl> wedges = this.getWedgeImpls();

		if (wedges == null) // something is wrong
		{
			// System.err.println("PersistenceHandlerWedge.restore: wedgesIterator == null");
			return;
		}

		// System.err.println("PersistenceHandlerWedge.restore: iterating");

		for (WedgeImpl wedgeImpl : wedges) {
			String wedgeIdentifier = wedgeImpl.getWedgeAttribute().getIdentifier();
			Map<String, Serializable> wedgeFields = meemContent.getPersistentFields(wedgeIdentifier);
			if (wedgeFields != null) {
				wedgeImpl.setWedgeContent(wedgeFields);
			}
		}
	}

	private DecoupledInvocationProcessor blockingDip = new DecoupledInvocationProcessor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.MeemContext#getWedgeIdentifier()
	 */
	public String getWedgeIdentifier() {
		throw new RuntimeException("not implemented yet, use meemContext");
	}

	/**
	 * The current running subject - may return null if there is no subject or the subject found appears to be dodgey.
	 * 
	 * @return the current running subject.
	 */
	private Subject getCurrentSubject() {
		return Subject.getSubject(java.security.AccessController.getContext());
	}

	/**
	 * Determines whether a Facet object is a valid proxy. That means it is a Proxy, the InvocationHandler is a MeemInvocationTarget and the target has not been revoked.
	 * 
	 * @param proxy
	 *            the proxy object to check.
	 * @return whether the proxy is a valid one.
	 */
	private boolean isValidProxy(Facet proxy) {
		if (proxy != null && Proxy.isProxyClass(proxy.getClass())) {
			InvocationHandler ih = Proxy.getInvocationHandler(proxy);

			if (ih != null && ih instanceof MeemInvocationTarget) {
				MeemInvocationTarget mit = (MeemInvocationTarget) ih;

				return mit.isValid();
			}
		}
		return false;
	}

}
