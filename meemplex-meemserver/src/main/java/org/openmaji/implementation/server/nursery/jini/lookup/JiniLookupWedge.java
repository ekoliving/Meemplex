/*
 * @(#)MeemRegistryGatewayJiniLookupWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.nursery.jini.lookup;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;
import org.openmaji.implementation.server.meem.FacetInformation;
import org.openmaji.implementation.server.meem.invocation.InvocationContext;
import org.openmaji.implementation.server.meem.invocation.InvocationContextTracker;
import org.openmaji.implementation.server.meem.wedge.remote.SmartProxyMeem;
import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.invocation.RemoteOutboundInvocationEvent;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.*;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import org.openmaji.meem.*;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.wedge.remote.RemoteMeem;


public class JiniLookupWedge implements Wedge, JiniLookup {	
	
	private static String LOOKUP_DELAY = "org.openmaji.server.jini.lookup.delayEnabled";
	
	public MeemContext meemContext;

	/* --------------------- conduits -------------------------- */
	
	public JiniLookupClient jiniLookupClientConduit;
	
	public JiniLookup jiniLookupConduit = this;

	
	
	private static Configuration configuration = null;

	private static LookupDiscoveryManager lookupDiscoveryManager = null;

	private static ServiceDiscoveryManager serviceDiscoveryManager = null;

	private LookupCache lookupCache = null;
	
	private MeemServiceItemFilter meemServiceItemFilter = null;

	private static Class<?>[] serviceInterfaces = new Class[] { Meem.class };

	private static ServiceTemplate serviceTemplate = new ServiceTemplate(null, serviceInterfaces, null);

	private ServiceDiscoveryListener serviceDiscoveryListener = new MeemServiceDiscoveryListener();
	
	
	/**
	 * 
	 */
	public void initialize() {
		/* ---------- Jini initialization ------------------------------------------ */

		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);

		if (majitekDirectory == null) {
			throw new RuntimeException("Empty Majitek directory property: " + Common.PROPERTY_MAJI_HOME);
		}

		if (configuration == null) {
			try {
				configuration =
					ConfigurationProvider.getInstance(
						new String[] { majitekDirectory + System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE)});
			} catch (ConfigurationException configurationException) {
				throw new RuntimeException("ConfigurationProviderException:" + configurationException);
			}
		}
		
		if (lookupDiscoveryManager == null) {
			try {
				lookupDiscoveryManager = new LookupDiscoveryManager(new String[] { 
						MeemSpace.getIdentifier()},
						null, // LookupLocator[]
						null, // DiscoveryListener
						configuration
				);
			} catch (IOException ioException) {
				throw new RuntimeException("LookupDiscoveryManager: IOException: " + ioException);
			}	catch (ConfigurationException configurationException) {
				throw new RuntimeException("ConfigurationException:" + configurationException);
			}
		}

		if (serviceDiscoveryManager == null) {
			if (lookupDiscoveryManager != null) {
				try {
					serviceDiscoveryManager = new ServiceDiscoveryManager(lookupDiscoveryManager, new LeaseRenewalManager(), configuration);
				} catch (IOException ioException) {
					throw new RuntimeException("ServiceDiscoveryManager: IOException: " + ioException);
				} catch (ConfigurationException configurationException) {
					throw new RuntimeException("ConfigurationProviderException:" + configurationException);
				}
			}
		}
	}


	public void startLookup(FacetItem facetItem, boolean returnLatestOnly) {
		
		// -mg- this is a real hack. 
		// If we don't have this sleep here, we get ClassCastExceptions from reggie. go figure

		boolean delay = Boolean.valueOf(System.getProperty(LOOKUP_DELAY, "false")).booleanValue();
		if (delay) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
		initialize();

		/* ---------- Jini Service lookup ------------------------------------------ */

		meemServiceItemFilter = new MeemServiceItemFilter(facetItem, returnLatestOnly);
		
		if (lookupCache == null) {
			if (serviceDiscoveryManager != null) {
				try {
					lookupCache = serviceDiscoveryManager.createLookupCache(serviceTemplate, meemServiceItemFilter, serviceDiscoveryListener);
				} catch (Exception exception) {
					throw new RuntimeException("LookupCache: Exception: " + exception);
				}
			}
		}
	}
	
	public void stopLookup() {
		if (serviceDiscoveryManager != null) {
			serviceDiscoveryManager.terminate();
		}
		serviceDiscoveryManager = null;
		lookupCache = null;
		meemServiceItemFilter = null;
	}

	private Meem extractMeem(ServiceItem serviceItem) {
		Meem meem = (Meem) serviceItem.service;

		try {
			ProxyPreparer proxyPreparer =
				(ProxyPreparer) configuration.getEntry(
					"net.jini.security.ProxyPreparer",
					"proxyPreparer",
					ProxyPreparer.class,
					new BasicProxyPreparer());

			meem = (Meem) proxyPreparer.prepareProxy(meem);

			return meem;
		} catch (ConfigurationException configurationException) {
			throw new RuntimeException("ConfigurationProviderException:" + configurationException);
		} catch (RemoteException remoteException) {
			throw new RuntimeException("RemoteException:" + remoteException);
		}
	}
	
	private boolean checkMeem(Meem meem) {
		InvocationHandler ih = Proxy.getInvocationHandler(meem);
		boolean isRemote = (ih instanceof SmartProxyMeem);

		if (isRemote) {
			RemoteMeem remoteMeem = ((SmartProxyMeem) ih).getRemoteMeem();
			
			MeemClient meemClient = new MeemClient() {
				public void referenceAdded(Reference arg0) {
				}
				public void referenceRemoved(Reference arg0) {
				}
			};
			
			Reference reference = Reference.spi.create("meemClientFacet", meemContext.getLimitedTargetFor(meemClient, MeemClient.class), false);
			try {
				MeemPath meemPath = meemContext.getSelf().getMeemPath();
				InvocationContextTracker.getInvocationContext().put(InvocationContext.CALLING_MEEM_PATH, meemPath);				
				InvocationContextTracker.getInvocationContext().put(RequestStack.REQUEST_STACK, RequestTracker.getRequestStack());
				
				Method method = Meem.class.getMethod("addOutboundReference", new Class[]{ Reference.class, Boolean.TYPE });
				
				if (DiagnosticLog.DIAGNOSE) {
					DiagnosticLog.log(new RemoteOutboundInvocationEvent(meemPath, meem.getMeemPath(), method, new Serializable[] {reference, Boolean.TRUE}));
				}
				
				remoteMeem.majikInvocation("meem","addOutboundReference", new Class[]{ Reference.class, Boolean.TYPE }, new Serializable[] { reference, Boolean.TRUE }, InvocationContextTracker.getInvocationContext());
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	
	class MeemServiceItemFilter implements ServiceItemFilter {
		
		private FacetItem facetItem;
		private boolean returnLatestOnly;
		private TimeStampEntry latestTimeStampEntry = null;
		
		
		public MeemServiceItemFilter(FacetItem facetItem, boolean returnLatestOnly) {
			this.facetItem = facetItem;
			this.returnLatestOnly = returnLatestOnly;			
		}
		
		public boolean check(ServiceItem serviceItem) {

			boolean match = false;

			Entry[] entries = serviceItem.attributeSets;

			for (int index = 0; index < entries.length; index++) {
				Entry entry = entries[index];

				if (entry instanceof FacetInformation) {
					FacetItem facetItem = ((FacetInformation) entry).facetItem;

					if (facetItem.equals(this.facetItem))
						match = true;
				}
			}
			
			if (match && returnLatestOnly) {
				for (int index = 0; index < entries.length; index++) {
					Entry entry = entries[index];
	
					if (entry instanceof TimeStampEntry) {
						TimeStampEntry timeStampEntry = (TimeStampEntry) entry;
	
						if (latestTimeStampEntry == null) {
							latestTimeStampEntry = timeStampEntry;
						} else {
							if (timeStampEntry.isNewer(latestTimeStampEntry)) {
								latestTimeStampEntry = timeStampEntry;
							} else {
								match = false;
							}
						}
					}
				}
			}
			
			return (match);
		}
	}
	
	/**
	 * 
	 */
	private class MeemServiceDiscoveryListener implements ServiceDiscoveryListener {
		/**
		 * @see net.jini.lookup.ServiceDiscoveryListener#serviceAdded(net.jini.lookup.ServiceDiscoveryEvent)
		 */
		public void serviceAdded(ServiceDiscoveryEvent event) {
			ServiceItem serviceItem = event.getPostEventServiceItem();

			if (serviceItem.service instanceof Meem) {
				Meem meem = extractMeem(serviceItem);
				
				if (checkMeem(meem)) {
//					System.err.println("ADDED: " + meem + " : " + meemServiceItemFilter.facetItem.interfaceName);
					jiniLookupClientConduit.meemAdded(meem);
				}
			}
		}

		/**
		 * @see net.jini.lookup.ServiceDiscoveryListener#serviceChanged(net.jini.lookup.ServiceDiscoveryEvent)
		 */
		public void serviceChanged(ServiceDiscoveryEvent event) {
			// -mg- Auto-generated method stub
		}
		
		/**
		 * @see net.jini.lookup.ServiceDiscoveryListener#serviceRemoved(net.jini.lookup.ServiceDiscoveryEvent)
		 */
		public void serviceRemoved(ServiceDiscoveryEvent event) {
			ServiceItem serviceItem = event.getPreEventServiceItem();

			if (serviceItem.service instanceof Meem) {
				Meem meem = extractMeem(serviceItem);
//				System.err.println("REMOVED: " + meem + " : " + meemServiceItemFilter.facetItem.interfaceName);
				if (Proxy.getInvocationHandler(meem) instanceof SmartProxyMeem) {
					((SmartProxyMeem)Proxy.getInvocationHandler(meem)).dispose();
				}
				jiniLookupClientConduit.meemRemoved(meem);
			}
		}
	}

}
