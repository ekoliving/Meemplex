/*
 * @(#)JiniMeemRegistryWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry.jini;

import java.rmi.RemoteException;
import java.util.*;

import org.openmaji.implementation.server.manager.registry.ScopedMeemPath;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.implementation.server.meem.wedge.remote.SmartProxyMeem;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.remote.RemoteMeem;
import org.openmaji.system.meem.wedge.remote.RemoteMeemClient;


/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class JiniMeemRegistryWedge implements Wedge, JiniMeemRegistry, MeemDefinitionProvider {

	//private static final Logger logger = Logger.getAnonymousLogger();
	
	public MeemCore meemCore;

	private Meem localRegistryGatewayMeem = null;

	public DependencyHandler dependencyHandlerConduit;

	// MeemPath = SmartProxyMeem
	private Map exportedMeems = Collections.synchronizedMap(new HashMap());

	// MeemPath = Meem
	private Map meemPathMap = Collections.synchronizedMap(new HashMap());

	private Map tasks = new HashMap();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public void commence() {
		localRegistryGatewayMeem = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());
	}
	
	public void conclude() {
		synchronized(tasks) {
			Iterator iter = tasks.values().iterator();
			while (iter.hasNext()) {
				MeemRegistryClientTask task = (MeemRegistryClientTask) iter.next();
				task.stop();
				iter.remove();
			}
		}
	}

	public JiniMeemRegistryClient jiniMeemRegistryClient;

	public final AsyncContentProvider<JiniMeemRegistryClient> jiniMeemRegistryClientProvider = new AsyncContentProvider<JiniMeemRegistryClient>() {
		public void asyncSendContent(JiniMeemRegistryClient client, Filter filter, ContentClient contentClient) {

			if (filter instanceof ExactMatchFilter) {
				Object template = ((ExactMatchFilter) filter).getTemplate();
				if (template instanceof MeemPath) {
					MeemPath meemPath = (MeemPath) template;
					startTask(meemPath, client, contentClient);
				}
			}
		}
	};

	public void startTask(MeemPath meemPath, JiniMeemRegistryClient client, ContentClient contentClient) {
		synchronized (tasks) {
			MeemRegistryClientTask task = (MeemRegistryClientTask) tasks.get(meemPath);
			if (task == null) {
				task = new MeemRegistryClientTask(meemPath);
				tasks.put(meemPath, task);
			}
			task.addClient(client, contentClient);
		}
	}

	public class MeemRegistryClientTask implements MeemRegistryClient, ContentClient {

		private Set clients = new HashSet();
		private Set contentClients = new HashSet();

		private final MeemPath meemPath;
		private DependencyAttribute dependencyAttribute;

		private boolean started = false;
		private boolean remoteMeemHandoff = false;
		private boolean doneSendContentSent = false;

		public MeemRegistryClientTask(MeemPath meemPath) {
			this.meemPath = meemPath;

			ScopedMeemPath scopedMeemPath = new ScopedMeemPath(meemPath, Scope.LOCAL);

			dependencyAttribute = new DependencyAttribute(
					DependencyType.WEAK, 
					Scope.LOCAL, 
					localRegistryGatewayMeem,
					"meemRegistryClient", 
					ExactMatchFilter.create(scopedMeemPath), 
					true
				);
		}

		private void start() {
			started = true;
			MeemRegistryClient proxy = (MeemRegistryClient) meemCore.getLimitedTargetFor(this, MeemRegistryClient.class);
			dependencyHandlerConduit.addDependency(proxy, dependencyAttribute, LifeTime.TRANSIENT);
		}

		private void stop() {
			dependencyHandlerConduit.removeDependency(dependencyAttribute);
			Meem smartProxyMeem = (Meem) exportedMeems.remove(meemPath);
			if (smartProxyMeem != null) {
				notifyClients(smartProxyMeem, false);
			}
			clients.clear();
			contentClients.clear();
		}

		public void addClient(JiniMeemRegistryClient client, ContentClient contentClient) {
			
			boolean contentSent = false;
			// check to see if we can notify this client already
			Meem smartProxyMeem = (Meem) exportedMeems.get(meemPath);
			if (smartProxyMeem != null) {
				try {
					client.meemRegisteredRemote(smartProxyMeem);
					if (contentClient != null) {
						contentClient.contentSent();
					}
					contentSent = true;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			if (contentClient != null && !contentSent) {
				if (doneSendContentSent) {
					contentClient.contentSent();
				} else {
					ContentClientPair pair = new ContentClientPair(client, contentClient);
					synchronized (contentClients) {
						contentClients.add(pair);
					}
				}
			} else {
				synchronized(clients) {
					clients.add(client);
				}
			}
			
			if (!started) {
				start();
			}
		}
		
		public void removeClient(JiniMeemRegistryClient client, ContentClient contentClient) {
			synchronized(clients) {
				clients.remove(client);
			}
			if (contentClient != null) {
				ContentClientPair pair = new ContentClientPair(client, contentClient);
				synchronized (contentClients) {
					contentClients.remove(pair);
				}
			}
		}

		public void meemDeregistered(Meem meem) {
			Meem exportedMeem = (Meem) meemPathMap.get(meem.getMeemPath());
			if (exportedMeem != null && exportedMeem == meem) {
				// we've seen this meem before and have already exported it
				Meem smartProxyMeem = (Meem) exportedMeems.remove(meem.getMeemPath());
				if (smartProxyMeem != null) {
					notifyClients(smartProxyMeem, true);
					return;
				}
			}

		}

		public void meemRegistered(Meem meem) {
			// check to see if we already have exported this meem

			Meem exportedMeem = (Meem) meemPathMap.get(meem.getMeemPath());
			if (exportedMeem != null && exportedMeem == meem) {
				// we've seen this meem before and have already exported it
				Meem smartProxyMeem = (Meem) exportedMeems.get(meem.getMeemPath());
				if (smartProxyMeem != null) {
					notifyClients(smartProxyMeem, true);
					return;
				}
			}

			remoteMeemHandoff = true;

			// turn it into a smart proxy meem

			// first, we have to get it as a remote meem

			RemoteMeemClient remoteMeemClient = (RemoteMeemClient) meemCore.getLimitedTargetFor(remoteMeemClientCallback,
					RemoteMeemClient.class);

			Reference remoteMeemClientReference = Reference.spi.create("remoteMeemClientFacet", remoteMeemClient, true);

			meem.addOutboundReference(remoteMeemClientReference, true);
		}

		public void contentFailed(String reason) {
			if (!remoteMeemHandoff) {
				synchronized (contentClients) {
					Iterator iter = contentClients.iterator();
					while (iter.hasNext()) {
						ContentClientPair contentClientPair = (ContentClientPair) iter.next();
						contentClientPair.contentClient.contentFailed(reason);
						iter.remove();
					}
				}
			}

		}

		public void contentSent() {
			if (!remoteMeemHandoff) {
				sendContentSent();
			}
		}

		private void sendContentSent() {
			doneSendContentSent = true;
			synchronized (contentClients) {
				Iterator iter = contentClients.iterator();
				while (iter.hasNext()) {
					ContentClientPair contentClientPair = (ContentClientPair) iter.next();
					contentClientPair.contentClient.contentSent();
					iter.remove();
				}
			}
		}

		private void notifyClients(Meem smartProxyMeem, boolean registered) {
			synchronized (clients) {
				Iterator iter = clients.iterator();
				while (iter.hasNext()) {
					JiniMeemRegistryClient client = (JiniMeemRegistryClient) iter.next();
					try {
						if (registered) {
							client.meemRegisteredRemote(smartProxyMeem);
						} else {
							client.meemDeregisteredRemote(smartProxyMeem);
						}
					} catch (RemoteException e) {
						// -mg- this seems to happen a bit to frequently for my liking. 
						// work out why this is happening and how to handle it properly.
						// maybe check the type of exception - if a "no such object in table" - trash it, if a connect exception, keep it
//						logger.log(Level.WARNING, "Exception while notifying clients about " + smartProxyMeem.getMeemPath(), e);
					}
				}
			}
			synchronized (contentClients) {
				Iterator iter = contentClients.iterator();
				while (iter.hasNext()) {
					ContentClientPair contentClientPair = (ContentClientPair) iter.next();
					JiniMeemRegistryClient client = contentClientPair.client;
					try {
						if (registered) {
							client.meemRegisteredRemote(smartProxyMeem);
						} else {
							client.meemDeregisteredRemote(smartProxyMeem);
						}
					} catch (RemoteException e) {
//						logger.log(Level.WARNING, "Exception while notifying contentClients about " + smartProxyMeem.getMeemPath(), e);
					}
				}
			}
		}
		
		class ContentClientPair {
			public final JiniMeemRegistryClient client;
			public final ContentClient contentClient;
			
			public ContentClientPair(JiniMeemRegistryClient client, ContentClient contentClient) {
				this.client = client;
				this.contentClient = contentClient;
			}

			public boolean equals(Object other) {
				ContentClientPair otherPair = (ContentClientPair) other;
				return otherPair.client.equals(client) && otherPair.contentClient.equals(contentClient);
			}
			
			public int hashCode() {
				return client.hashCode() ^ contentClient.hashCode();
			}
		}

		public RemoteMeemClient remoteMeemClientCallback = new RemoteMeemClient() {

			public void remoteMeemChanged(Meem meem, RemoteMeem remoteMeem, FacetItem[] facetItems) {

				MeemPath meemPath = meem.getMeemPath();

				synchronized (meemPathMap) {
					Meem exportedMeem = (Meem) meemPathMap.get(meem.getMeemPath());
					if (exportedMeem != null && exportedMeem == meem) {

						// we've seen this meem before and have already exported it
						Meem smartProxyMeem = (Meem) exportedMeems.get(meem.getMeemPath());
						if (smartProxyMeem != null) {
							notifyClients(smartProxyMeem, true);
							sendContentSent();
							return;
						}
					}

					remoteMeem = (RemoteMeem) ExporterHelper.export(remoteMeem);

					Meem smartProxyMeem = new SmartProxyMeem(remoteMeem, meemPath).getSmartProxyMeem();

					exportedMeems.put(meemPath, smartProxyMeem);
					meemPathMap.put(meemPath, meem);

					notifyClients(smartProxyMeem, true);
					sendContentSent();
				}
			}
		};
	}

	public MeemClient meemReferenceClientConduit = new MeemClient() {
		public void referenceAdded(Reference reference) {
			Facet target = reference.getTarget();
			if (target instanceof JiniMeemRegistryClient) {
				JiniMeemRegistryClient client = (JiniMeemRegistryClient) target;
				MeemPath meemPath = (MeemPath) ((ExactMatchFilter) reference.getFilter()).getTemplate();

				startTask(meemPath, client, null);
			}
		}

		public void referenceRemoved(Reference reference) {
			Facet target = reference.getTarget();
			if (target instanceof JiniMeemRegistryClient) {
				JiniMeemRegistryClient client = (JiniMeemRegistryClient) target;
				MeemPath meemPath = (MeemPath) ((ExactMatchFilter) reference.getFilter()).getTemplate();

				MeemRegistryClientTask task = (MeemRegistryClientTask) tasks.get(meemPath);
				if (task != null) {
					task.removeClient(client, null);
				}
			}
		}
	};

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return (meemDefinition);
	}

}