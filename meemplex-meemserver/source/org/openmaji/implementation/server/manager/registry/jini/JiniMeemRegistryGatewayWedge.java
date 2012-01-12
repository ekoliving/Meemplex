/*
 * @(#)JiniMeemRegistryGatewayWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry.jini;

import java.rmi.RemoteException;
import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryGatewayWedge;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.invocation.RevokableTarget;
import org.openmaji.implementation.server.meem.wedge.lifecycle.SystemLifeCycleClientAdapter;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.implementation.server.meem.wedge.remote.SmartProxyMeem;

import org.openmaji.meem.*;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class JiniMeemRegistryGatewayWedge implements Wedge, MeemRegistryGateway {
	public MeemCore meemCore;
	
	public DependencyHandler dependencyHandlerConduit;

	private final Map cache = new HashMap();
	
	private final MeemRegistryClient emptyMeemRegistryClient = new MeemRegistryClient() {
		public void meemDeregistered(Meem meem) {}
		public void meemRegistered(Meem meem) {}
	};
	
	public MeemRegistryClient meemRegistryClient;
	public final AsyncContentProvider meemRegistryClientProvider = new AsyncContentProvider() {
		public void asyncSendContent(Object target, Filter filter, ContentClient contentClient) {
			if (getMeemRegistries().length == 0) {
				contentClient.contentSent();
				return;
			}
			
			MeemRegistryClient client = (MeemRegistryClient) target;

			MeemPath meemPath = null;
			if (filter instanceof ExactMatchFilter) {
				Object template = ((ExactMatchFilter) filter).getTemplate();
				if (template instanceof MeemPath) {
					meemPath = (MeemPath) template;
				}
			}

			// check to see if the path being asked for is something we already know about
			Meem registryMeem = checkMeemRegistries(meemPath);

			if (registryMeem != null) {
				client.meemRegistered(registryMeem);
				contentClient.contentSent();
				return;
			}
			
			Meem meem = checkCache(meemPath);
			if (meem != null) {
				client.meemRegistered(meem);
				contentClient.contentSent();
				return;
			}
			
			new JiniMeemRegistryClientTask(meemPath, getMeemRegistries(), client, contentClient);
			addPathToWatch(meemPath, emptyMeemRegistryClient);
		}

		private Meem checkMeemRegistries(MeemPath meemPath) {
			synchronized (registries) {
				Iterator i = registries.entrySet().iterator();
				while (i.hasNext()) {
					Map.Entry entry = (Map.Entry)i.next();
					MeemPath registryMeemPath = (MeemPath)entry.getKey();
					if (registryMeemPath.equals(meemPath)) {
						return (Meem)entry.getValue();
					}
				}
			}
			return null;
		}
	};

	private static final Logger logger = LogFactory.getLogger();
	private static final int LOG_LEVEL = Common.getLogLevelVerbose();

	private static JiniMeemRegistryGatewayWedge instance = null;

	private static final Map registries = Collections.synchronizedMap(new HashMap());
	
	private static final Map monitoredPaths = new HashMap();
	
	public LifeCycleClient lifeCycleClientConduit = new SystemLifeCycleClientAdapter(this);

	private boolean added = false;
	public void commence() {
		if (!added) {
			MeemRegistryGatewayWedge.addRemoteRegistry(meemCore.getSelf());			
			added = true;
		}
	}

	public JiniMeemRegistryGatewayWedge() {
		if (instance != null) {
			System.err.println("*** More than one JiniMeemRegistryGatewayWedge created ***");
		}

		instance = this;
	}

	// Helper for getting all known MeemRegistries
	private static Meem[] getMeemRegistries() {
		Meem[] meems = null;

		synchronized (registries) {
			meems = (Meem[]) registries.values().toArray(new Meem[registries.size()]);
		}

		return meems;
	}

	

	public static void addRemoteRegistry(Meem registryMeem) {
		if (registries.containsKey(registryMeem.getMeemPath())) {
			LogTools.trace(logger, LOG_LEVEL, "addRemoteRegistry(): ignoring - already present - " + registryMeem.getMeemPath());
			return;
		}

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMREGISTRY) {
			LogTools.trace(logger, LOG_LEVEL, "addRemoteRegistry(): " + registryMeem.getMeemPath());
		}
		
		registries.put(registryMeem.getMeemPath(), registryMeem);
		
		Set tasks;
		synchronized(monitoredPaths) {
			tasks = new HashSet(monitoredPaths.values());
		}
		
		Iterator i = tasks.iterator();
		while (i.hasNext()) {
			JiniMeemRegistryMonitorTask jiniMeemRegistryMonitorTask = (JiniMeemRegistryMonitorTask) i.next();
			jiniMeemRegistryMonitorTask.registryAdded(registryMeem);
		}
	}
	
	public static void removeRemoteRegistry(Meem registryMeem) {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMREGISTRY) {
			LogTools.trace(logger, LOG_LEVEL, "removeRemoteRegistry(): " + registryMeem.getMeemPath());
		}

		registries.remove(registryMeem.getMeemPath());
		
		Set tasks;
		synchronized(monitoredPaths) {
			tasks = new HashSet(monitoredPaths.values());
		}
		
		Iterator i = tasks.iterator();
		while (i.hasNext()) {
			JiniMeemRegistryMonitorTask jiniMeemRegistryMonitorTask = (JiniMeemRegistryMonitorTask) i.next();
			jiniMeemRegistryMonitorTask.registryRemoved(registryMeem);
		}
	}
	
	private Meem addToCache(Meem meem) {
		synchronized(cache) {
			if (!cache.containsKey(meem.getMeemPath())) {
				cache.put(meem.getMeemPath(), meem);
				return meem;
			} else {
				return (Meem) cache.get(meem.getMeemPath());
			}
		}
	}
	
	private void removeFromCache(Meem meem) {
		synchronized(cache) {
			Object obj = cache.remove(meem.getMeemPath());
			if (obj instanceof SmartProxyMeem) {
				((SmartProxyMeem)obj).dispose();
			}
		}
	}
	
	private Meem checkCache(MeemPath meemPath) {
		synchronized(cache) {
			return (Meem) cache.get(meemPath);
		}
	}

	public class JiniMeemRegistryClientTask implements JiniMeemRegistryClient {
		private Meem[] meemRegistries;
		private MeemRegistryClient client;
		private int index;
		private MeemPath meemPath;
		private Meem meem;
		private JiniMeemRegistryClient proxyRemote = null;
		private RevokableTarget revokableTarget = null;
		private final ContentClient contentClient;

		public JiniMeemRegistryClientTask(MeemPath meemPath, Meem[] meemRegistries, MeemRegistryClient client, ContentClient contentClient) {
			this.meemRegistries = meemRegistries;
			this.meemPath = meemPath;
			this.client = client;
			this.contentClient = contentClient;
			this.index = 0;

			proceed();
		}

		public void contentSent() throws RemoteException {
			complete();
			if (meem != null) {
				client.meemRegistered(meem);
				contentClient.contentSent();
			}
			else {
				proceed();
			}
		}

		public void contentFailed(String reason) throws RemoteException {
			complete();
			contentClient.contentFailed(reason);
		}

		public void meemDeregisteredRemote(Meem meem) throws RemoteException {
			this.meem = null;
			
			removeFromCache(meem);			
		}

		public void meemRegisteredRemote(Meem meem) throws RemoteException {
			this.meem = meem;
			addToCache(meem);
		}

		private void proceed() {
			if (index >= meemRegistries.length) {
				contentClient.contentSent();
			}
			else {
				proxyRemote = (JiniMeemRegistryClient)
					meemCore.getLimitedTargetFor(this, JiniMeemRegistryClient.class);

				revokableTarget = RevokableExporterHelper.export(proxyRemote);

				Filter filter = new ExactMatchFilter(meemPath);

				Reference referenceRemote = Reference.spi.create(
					"jiniMeemRegistryClient", revokableTarget.getTarget(), true, filter);

				meemRegistries[index++].addOutboundReference(referenceRemote, true);
			}
		}

		private void complete() {
			((MeemCoreImpl) meemCore).revokeTargetProxy(proxyRemote, this);
			proxyRemote = null;

			revokableTarget.revoke();
			revokableTarget = null;
		}
	}

	/*----------------- MeemResolver methods ------------------*/
	
	public MeemClient meemReferenceClientConduit = new MeemClient()
	{
		public void referenceAdded(Reference reference)
		{
			Facet target = reference.getTarget();
			if (target instanceof MeemRegistryClient)
			{
				MeemRegistryClient client = (MeemRegistryClient) target;
				MeemPath meemPath = (MeemPath) ((ExactMatchFilter) reference.getFilter()).getTemplate();
				
				addPathToWatch(meemPath, client);
			}
		}

		public void referenceRemoved(Reference reference)
		{
			Facet target = reference.getTarget();
			if (target instanceof MeemRegistryClient)
			{
				MeemRegistryClient client = (MeemRegistryClient) target;
				MeemPath meemPath = (MeemPath) ((ExactMatchFilter) reference.getFilter()).getTemplate();

				terminateMonitor(meemPath, client);
			}
		}
	};
		
	private void addPathToWatch(MeemPath meemPath, MeemRegistryClient client) {
	
		boolean startMonitoring = false;

		synchronized(monitoredPaths) {
			JiniMeemRegistryMonitorTask jiniMeemRegistryMonitorTask = (JiniMeemRegistryMonitorTask) monitoredPaths.get(meemPath); 
			
			if (jiniMeemRegistryMonitorTask == null) {
				jiniMeemRegistryMonitorTask = new JiniMeemRegistryMonitorTask(meemPath);
				monitoredPaths.put(meemPath, jiniMeemRegistryMonitorTask);
				
				startMonitoring = true;
			} else {
				
				jiniMeemRegistryMonitorTask.addClient(client);
			}
			
			if (startMonitoring) {
				JiniMeemRegistryClient clientTask = (JiniMeemRegistryClient) meemCore.getLimitedTargetFor(jiniMeemRegistryMonitorTask, JiniMeemRegistryClient.class);
		
				jiniMeemRegistryMonitorTask.setClientTask(clientTask);
				
				jiniMeemRegistryMonitorTask.addClient(client);
			}
		}
		
		Meem meem = checkCache(meemPath);
		if (meem != null) {
			client.meemRegistered(meem);
		}		
	}
	
	private void terminateMonitor(MeemPath meemPath, MeemRegistryClient client) {
		JiniMeemRegistryMonitorTask meemRegistryMontiorTask = (JiniMeemRegistryMonitorTask) monitoredPaths.get(meemPath); 
		if (meemRegistryMontiorTask != null) {
			meemRegistryMontiorTask.removeClient(client);
		}	
	}
		
	public class JiniMeemRegistryMonitorTask implements JiniMeemRegistryClient {
		private Set clients = new HashSet();
		private final MeemPath meemPath;
		private Reference reference;
		private boolean listening = false;
		private RevokableTarget revokableTarget = null;
		
		protected JiniMeemRegistryMonitorTask(MeemPath meemPath) {
			this.meemPath = meemPath;
		}
		
		protected void setClientTask(JiniMeemRegistryClient clientTask) {
			revokableTarget = RevokableExporterHelper.export(clientTask);

			Filter filter = new ExactMatchFilter(meemPath);

			this.reference = Reference.spi.create(
				"jiniMeemRegistryClient", revokableTarget.getTarget(), true, filter);
		}
		
		protected void addClient(MeemRegistryClient client) {
			clients.add(client);			
			if (!listening) {
				start();
			} else if (checkCache(meemPath) != null) {
				client.meemRegistered(checkCache(meemPath));
			}
		}

		protected void removeClient(MeemRegistryClient client) {
			clients.remove(client);			
			if (clients.isEmpty()) {
				stop();
			}
		}
		
		private void start() {
			listening = true;
			
			Meem[] meemRegistries = getMeemRegistries();

			for (int i = 0; i < meemRegistries.length; i++) {
				meemRegistries[i].addOutboundReference(reference, false);
			}
		}
		
		private void stop() {
			
			listening = false;
			
			Meem[] meemRegistries = getMeemRegistries();

			for (int i = 0; i < meemRegistries.length; i++) {
				meemRegistries[i].removeOutboundReference(reference);
			}
		}

		public void meemRegisteredRemote(Meem meem) throws RemoteException {
			meem = addToCache(meem);
			synchronized(clients) {
				for (Iterator i = clients.iterator(); i.hasNext(); ) {
					MeemRegistryClient client = (MeemRegistryClient)i.next();
					client.meemRegistered(meem);
				}
			}
		}
		
		public void meemDeregisteredRemote(Meem meem) throws RemoteException {
			removeFromCache(meem);
			synchronized(clients) {
				for (Iterator i = clients.iterator(); i.hasNext(); ) {
					((MeemRegistryClient)i.next()).meemDeregistered(meem);
				}
			}
		}

		/**
		 * @see org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistryClient#contentSent()
		 */
		public void contentSent() throws RemoteException {
			synchronized(clients) {
				for (Iterator i = clients.iterator(); i.hasNext(); ) {
					Object client = i.next();
					if (client instanceof ContentClient) {
						((ContentClient)client).contentSent();
					}
				}
			}
		}

		public void contentFailed(String reason) throws RemoteException {
			synchronized(clients) {
				for (Iterator i = clients.iterator(); i.hasNext(); ) {
					Object client = i.next();
					if (client instanceof ContentClient) {
						((ContentClient)client).contentFailed(reason);
					}
				}
			}		
		}

		protected void registryAdded(Meem meemRegistryMeem) {
			meemRegistryMeem.addOutboundReference(reference, false);
		}
		
		protected void registryRemoved(Meem meemRegistryMeem) {
			meemRegistryMeem.removeOutboundReference(reference);
		}
	}

}
