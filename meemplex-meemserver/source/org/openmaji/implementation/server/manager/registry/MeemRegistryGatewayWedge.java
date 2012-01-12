/*
 * @(#)MeemRegistryGatewayWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry;

import java.util.*;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.wedge.lifecycle.SystemLifeCycleClientAdapter;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.implementation.server.space.CategoryWedge;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.swzoo.log2.core.*;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemRegistryGatewayWedge
  implements MeemRegistryGateway, MeemDefinitionProvider, Wedge {

	public MeemCore meemCore;

  private static MeemRegistryGatewayWedge
    meemRegistryGatewayWedgeSingleton = null;

  public LifeCycle lifeCycleConduit;

  public LifeCycleClient lifeCycleClientConduit =
    new SystemLifeCycleClientAdapter(this);
  
  public DependencyHandler dependencyHandlerConduit;

  public synchronized void commence() {
    if (meemRegistryGatewayWedgeSingleton != null) {
      LogTools.error(
        logger,
        "Another MeemRegistryGateway Meem already commenced. " +
        "Self-destruct sequence initiated !"
      );

      lifeCycleConduit.changeLifeCycleState(LifeCycleState.ABSENT);
    }
    else {
      meemRegistryGatewayWedgeSingleton = this;
    }
  }

	// note in this case contentSent actually means "I've received the request" - since this is
	// an essential meem we can get away with this...

	public MeemRegistryClient meemRegistryClient;

	public final AsyncContentProvider meemRegistryClientProvider = new AsyncContentProvider()
	{
		public void asyncSendContent(Object target, Filter filter, ContentClient contentClient)
		{
			MeemRegistryClient meemRegistryClientTarget = (MeemRegistryClient) target;

			MeemPath meemPath = null;
			Scope scope = null;
			if (filter instanceof ExactMatchFilter) {
				Object template = ((ExactMatchFilter) filter).getTemplate();
				if (template instanceof MeemPath) {
					meemPath = (MeemPath) template;
					scope = Scope.DISTRIBUTED;
				}
				if (template instanceof ScopedMeemPath) {
					meemPath = ((ScopedMeemPath) template).getMeemPath();
					scope = ((ScopedMeemPath) template).getScope();
				}
			}

			// check to see if the path being asked for is something we already know about
			Meem registryMeem = checkMeemRegistries(meemPath);

			if (registryMeem != null) {
				meemRegistryClientTarget.meemRegistered(registryMeem);
				contentClient.contentSent();
			}
			else {
				new MeemRegistryClientTask(meemPath, getMeemRegistries(scope),
					meemRegistryClientTarget, contentClient);
			}
		}

		private Meem checkMeemRegistries(MeemPath meemPath) {
			synchronized (registryList) {
				Iterator i = registryList.iterator();
				while (i.hasNext()) {
					Meem registry = (Meem) i.next();
					if (registry.getMeemPath().equals(meemPath)) {
						return registry;
					}
				}
			}
			return null;
		}
	};

	public MeemClientConduit meemClientConduit;

	/**
	 * Map of MeemRegistry Meems
	 */
	private static List registryListLocal = Collections.synchronizedList(new ArrayList());
	private static List registryList = Collections.synchronizedList(new ArrayList());
	
	private static Map monitoredPaths = Collections.synchronizedMap(new HashMap());

	// Helper for getting all known MeemRegistries
	public static Meem[] getMeemRegistries()
	{
		Meem[] meems = null;

		synchronized (registryList)
		{
			meems = (Meem[]) registryList.toArray(new Meem[registryList.size()]);
		}

		return meems;
	}

	private Meem[] getMeemRegistries(Scope scope) {
		Meem[] meems = null;

		if (scope.equals(Scope.LOCAL)) {
			synchronized (registryListLocal) {
				meems = (Meem[]) registryListLocal.toArray(new Meem[registryListLocal.size()]);
			}

			return meems;
		}

		return getMeemRegistries();
	}

	private static void addRegistry(Meem registryMeem, boolean local)
	{
		if (registryList.contains(registryMeem))
		{
			LogTools.trace(logger, logLevelVerbose, "addRegistry(): ignoring - already present - " + registryMeem.getMeemPath());
			return;
		}

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMREGISTRY)
		{
			LogTools.trace(logger, logLevelVerbose, "addRegistry(): " + registryMeem.getMeemPath());
		}

		//
		// the zeroeth spot is reserved for the meemRegistry always added first.
		//
		if (local && registryList.size() > 1)
		{
			registryList.add(1, registryMeem);
		}
		else
		{
			registryList.add(registryMeem);
		}

		if (local)
		{
			registryListLocal.add(registryMeem);
		}
		
		Set tasks;
		synchronized(monitoredPaths) {
			tasks = new HashSet(monitoredPaths.values());
		}
		
		Scope scope = null;
		if (local) {
			scope = Scope.LOCAL;
		} else {
			scope = Scope.DISTRIBUTED;
		}

		Iterator i = tasks.iterator();
		while (i.hasNext()) {
			MeemRegistryMonitorTask meemRegistryMonitorTask = (MeemRegistryMonitorTask) i.next();
			meemRegistryMonitorTask.registryAdded(registryMeem, scope);
		}
	}

	/**
	 * Ideally we'd just pass the meem here, rather than assume we can cast meem from the
	 * resolver facet, the problem is that to get the resolver facet we need to resolve the meem,
	 * which involves more than just the registry...
	 * <p>
	 * Registries are searched in the order added, unless added using addLocalRegistry below.
	 *
	 * @param registryMeem Registry meem.
	 */
	public static void addRemoteRegistry(Meem registryMeem)
	{
		addRegistry(registryMeem, false);
	}

	/**
	 * Add a registry known to be local to the VM. The effect of this is that the registry gets
	 * added to the front of the registry list rather than the end.
	 *
	 * @param registryMeem  Registry meem.
	 */
	public static void addLocalRegistry(Meem registryMeem)
	{
		addRegistry(registryMeem, true);
	}

	public class MeemRegistryClientTask implements MeemRegistryClient, ContentClient
	{
		private final MeemPath meemPath;
		private final Meem[] meemRegistries;
		private final MeemRegistryClient client;
		private final ContentClient contentClient;
		private int index = 0;
		private Meem meem = null;
		private MeemRegistryClient proxy = null;

		public MeemRegistryClientTask(
			MeemPath meemPath,
			Meem[] meemRegistries,
			MeemRegistryClient client,
			ContentClient contentClient)
		{
			this.meemPath = meemPath;
			this.meemRegistries = meemRegistries;
			this.client = client;
			this.contentClient = contentClient;

			proceed();
		}

		public void meemRegistered(Meem meem)
		{
			this.meem = meem;
		}

		/**
		 * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemDeregistered(org.openmaji.meem.Meem)
		 */
		public void meemDeregistered(Meem meem)
		{
			this.meem = null;
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent()
		{
			complete();

			if (meem == null)
			{
				proceed();
			}
			else
			{
				client.meemRegistered(meem);
				contentClient.contentSent();
			}
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
		 */
		public void contentFailed(String reason)
		{
			complete();
			contentClient.contentFailed(reason);
		}

		private void proceed()
		{
			if (index >= meemRegistries.length)
			{
				contentClient.contentSent();
			}
			else
			{
				proxy = (MeemRegistryClient) meemCore.getLimitedTargetFor(
					this, MeemRegistryClient.class);

				Reference reference = Reference.spi.create("meemRegistryClient", proxy, true,
					new ExactMatchFilter(meemPath));

				meemRegistries[index++].addOutboundReference(reference, true);
			}
		}

		private void complete()
		{
			((MeemCoreImpl) meemCore).revokeTargetProxy(proxy, this);
			proxy = null;
		}
	}

/* --------- Monitor --------------- */

	public MeemClient meemReferenceClientConduit = new MeemClient()
	{
		public void referenceAdded(Reference reference)
		{
			Facet target = reference.getTarget();
			if (target instanceof MeemRegistryClient)
			{
				MeemRegistryClient client = (MeemRegistryClient) target;
				Object template = ((ExactMatchFilter) reference.getFilter()).getTemplate();
				
				ScopedMeemPath scopedMeemPath;
				if (template instanceof MeemPath) {
					scopedMeemPath = new ScopedMeemPath((MeemPath)template, Scope.DISTRIBUTED);
				} else 
				if (template instanceof ScopedMeemPath) {
					scopedMeemPath = (ScopedMeemPath) template;
				} else {
					throw new RuntimeException("Unknown template type " + template.getClass().getName());
				}
				
				addPathToWatch(scopedMeemPath, client);
			}
		}

		public void referenceRemoved(Reference reference)
		{
			Facet target = reference.getTarget();
			if (target instanceof MeemRegistryClient)
			{
				MeemRegistryClient client = (MeemRegistryClient) target;
				Object template = ((ExactMatchFilter) reference.getFilter()).getTemplate();

				ScopedMeemPath scopedMeemPath;
				if (template instanceof MeemPath) {
					scopedMeemPath = new ScopedMeemPath((MeemPath)template, Scope.DISTRIBUTED);
				} else 
				if (template instanceof ScopedMeemPath) {
					scopedMeemPath = (ScopedMeemPath) template;
				} else {
					throw new RuntimeException("Unknown template type " + template.getClass().getName());
				}

				terminateMonitor(scopedMeemPath, client);
			}
		}
	};
	
	private void addPathToWatch(ScopedMeemPath scopedMeemPath, MeemRegistryClient client) {

		MeemRegistryMonitorTask meemRegistryMonitorTask;
		synchronized (monitoredPaths) {
			meemRegistryMonitorTask = (MeemRegistryMonitorTask) monitoredPaths.get(scopedMeemPath); 
			
			if (meemRegistryMonitorTask == null) {
				meemRegistryMonitorTask = new MeemRegistryMonitorTask(scopedMeemPath);
				monitoredPaths.put(scopedMeemPath, meemRegistryMonitorTask);
			}
		}

		meemRegistryMonitorTask.addClient(client);
	}
	
	private void terminateMonitor(ScopedMeemPath scopedMeemPath, MeemRegistryClient client) {
		MeemRegistryMonitorTask meemRegistryMontiorTask = (MeemRegistryMonitorTask) monitoredPaths.get(scopedMeemPath); 
		if (meemRegistryMontiorTask != null) {
			meemRegistryMontiorTask.removeClient(client);
		}	
	}
	
	private class MeemRegistryMonitorTask implements MeemRegistryClient {
		private Set clients = new HashSet();
		private final ScopedMeemPath scopedMeemPath;
		private boolean listening = false;
		private Map dependencyAttributes = new HashMap();
		private MeemRegistryClient clientTask;
		private Meem foundMeem = null;
	
		public MeemRegistryMonitorTask(ScopedMeemPath scopedMeemPath) {
			this.scopedMeemPath = scopedMeemPath;

			clientTask = (MeemRegistryClient) meemCore.getLimitedTargetFor(this, MeemRegistryClient.class);			
		}

		public void addClient(MeemRegistryClient client) {
			synchronized(clients) {
				clients.add(client);		
			}
			if (!listening) {
				start();
			} else if (foundMeem != null){
				client.meemRegistered(foundMeem);
			}
		}

		public void removeClient(MeemRegistryClient client) {
			synchronized(clients) {
				clients.remove(client);
			}
			if (clients.isEmpty()) {
				stop();
			}
		}
		
		private void start() {
			if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
				LogTools.trace(logger, logLevelVerbose, "*** watching: " + scopedMeemPath);
			}

			listening = true;
			
			Meem[] meemRegistries = getMeemRegistries(scopedMeemPath.getScope());

			for (int i = 0; i < meemRegistries.length; i++) {
				DependencyAttribute dependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meemRegistries[i], "meemRegistryClient", new ExactMatchFilter(scopedMeemPath.getMeemPath()), true);
				dependencyAttributes.put(meemRegistries[i], dependencyAttribute);
				dependencyHandlerConduit.addDependency(clientTask, dependencyAttribute, LifeTime.TRANSIENT);
			}
		}
		
		private void stop() {
			listening = false;
			
			Meem[] meemRegistries = getMeemRegistries(scopedMeemPath.getScope());

			for (int i = 0; i < meemRegistries.length; i++) {				
				DependencyAttribute dependencyAttribute = (DependencyAttribute) dependencyAttributes.remove(meemRegistries[i]);
				if (dependencyAttribute != null) {
					dependencyHandlerConduit.removeDependency(dependencyAttribute);
				}
			}
		}

		/**
		 * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemRegistered(org.openmaji.meem.Meem)
		 */
		public void meemRegistered(Meem meem) {
			foundMeem = meem;
			synchronized(clients) {
				for (Iterator i = clients.iterator(); i.hasNext(); ) {
					MeemRegistryClient client = (MeemRegistryClient) i.next();		
					client.meemRegistered(meem);
				}
			}
		}
		
		/**
		 * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemDeregistered(org.openmaji.meem.Meem)
		 */
		public void meemDeregistered(Meem meem) {
			foundMeem = null;
			synchronized(clients) {
				for (Iterator i = clients.iterator(); i.hasNext(); ) {
					((MeemRegistryClient)i.next()).meemDeregistered(meem);
				}
			}
		}
		
		public void registryAdded(Meem meemRegistryMeem, Scope scope) {
			if (scopedMeemPath.getScope().equals(scope) || scopedMeemPath.getScope().equals(Scope.DISTRIBUTED)) {
				DependencyAttribute dependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meemRegistryMeem, "meemRegistryClient", new ExactMatchFilter(scopedMeemPath.getMeemPath()), true);
				dependencyAttributes.put(meemRegistryMeem, dependencyAttribute);
				dependencyHandlerConduit.addDependency(clientTask, dependencyAttribute, LifeTime.TRANSIENT);
			}
		}
		
		public void registryRemoved(Meem meemRegistryMeem, Scope scope) {
			if (scopedMeemPath.getScope().equals(scope) || scopedMeemPath.getScope().equals(Scope.DISTRIBUTED)) {
				DependencyAttribute dependencyAttribute = (DependencyAttribute) dependencyAttributes.remove(meemRegistryMeem);
				if (dependencyAttribute != null) {
					dependencyHandlerConduit.removeDependency(dependencyAttribute);
				}
			}
		}
	}

/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] { this.getClass(), CategoryWedge.class}
      );
    }

    return(meemDefinition);
  }

/* ---------- Logging fields ----------------------------------------------- */

  /**
   * Create the per-class Software Zoo Logging V2 reference.
   */

  private static final Logger logger = LogFactory.getLogger();

  /**
   * Acquire the Maji system-wide verbose logging level.
   */

  private static final int logLevelVerbose = Common.getLogLevelVerbose();
}
