/*
 * @(#)MeemStoreImporterWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.nursery.jini.meemstore;

import java.rmi.RemoteException;
import java.util.*;
import java.util.Map.Entry;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;
import net.jini.config.NoSuchEntryException;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryGroupManagement;
import net.jini.discovery.LookupDiscovery;
import net.jini.lookup.ServiceDiscoveryManager;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.space.meemstore.MeemStoreClient;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MeemStoreImporterWedge implements MeemStore, MeemStoreCallBack, Wedge {

	public MeemStoreClient meemStoreClient; // Outbound Facet

	public MeemContentClient meemContentClient; // Outbound Facet

	// Inner class defined below
	public final ContentProvider<MeemContentClient> meemContentClientProvider = new MeemContentClientContentProvider();

	public MeemDefinitionClient meemDefinitionClient; // Outbound Facet

	// Inner class defined below
	public final ContentProvider<MeemDefinitionClient> meemDefinitionClientProvider = new MeemDefinitionClientContentProvider(); 

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	private static MeemStoreImporterWedge meemStoreImporterWedge; // Singleton

	private MeemStoreCallForward meemStoreCallForward = null;

	private static final Logger logger = Logger.getAnonymousLogger();

	private Configuration configuration = null;

	private ServiceDiscoveryManager serviceDiscoveryManager = null;

	private static Class<?>[] serviceInterfaces = new Class[] { MeemStoreCallForward.class };

	private static ServiceTemplate serviceTemplate = new ServiceTemplate(null, serviceInterfaces, null);

	public static MeemStoreImporterWedge getInstance() {
		if (meemStoreImporterWedge == null) {
			throw new RuntimeException("meemStoreImporterWedge not instantiated");
		}

		return (meemStoreImporterWedge);
	}

	public MeemStoreImporterWedge() {
		meemStoreImporterWedge = this;
	}

	public void commence() {
		/*
		 * ---------- Jini initialization
		 * ------------------------------------------
		 */

		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);

		if (majitekDirectory == null) {
			throw new RuntimeException("Empty Majitek directory property: " + Common.PROPERTY_MAJI_HOME);
		}

		try {
			configuration = ConfigurationProvider.getInstance(new String[] { majitekDirectory + System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE) });

			if (serviceDiscoveryManager == null) {
				try {
					serviceDiscoveryManager = (ServiceDiscoveryManager) configuration.getEntry("org.openmaji.implementation.server.nursery.jini.meemstore.MeemStoreExporterWedge", "serviceDiscovery",
							ServiceDiscoveryManager.class);
				}
				catch (NoSuchEntryException noSuchEntryException) {
					/* Default to search in the public group */
					serviceDiscoveryManager = new ServiceDiscoveryManager(new LookupDiscovery(DiscoveryGroupManagement.ALL_GROUPS, configuration), null, configuration);
				}
			}

			/*
			 * ---------- Jini Service lookup
			 * ------------------------------------------
			 */

			ServiceItem serviceItem = serviceDiscoveryManager.lookup(serviceTemplate, null, Long.MAX_VALUE);

			meemStoreCallForward = (MeemStoreCallForward) serviceItem.service;

			logger.log(Level.INFO, "Jini Service lookup: " + serviceItem.serviceID);
		}
		catch (Exception exception) {
			throw new RuntimeException("Exception:" + exception);
		}
	}

	public void conclude() {
	}

	/* ---------- Inbound Facet: MeemStore ------------------------------------- */

	public void storeMeemContent(MeemPath meemPath, MeemContent meemContent) {

		try {
			logger.log(Level.INFO, "# storeMeemContent(" + meemPath + ")");

			meemStoreCallForward.storeMeemContent(meemPath, meemContent);
		}
		catch (Exception exception) {
			logger.log(Level.WARNING, "# MeemStoreCallForward: " + exception);
		}
	}

	public void storeMeemDefinition(MeemPath meemPath, MeemDefinition meemDefinition) {

		try {
			logger.log(Level.INFO, "# storeMeemDefinition(" + meemPath + ")");

			meemStoreCallForward.storeMeemDefinition(meemPath, meemDefinition);
		}
		catch (Exception exception) {
			logger.log(Level.WARNING, "# MeemStoreCallForward: " + exception);
		}
	}

	public void destroyMeem(MeemPath meemPath) {

		try {
			logger.log(Level.INFO, "# destroyMeem(" + meemPath + ")");

			meemStoreCallForward.destroyMeem(meemPath);
		}
		catch (Exception exception) {
			logger.log(Level.WARNING, "# MeemStoreCallForward: " + exception);
		}
	}

	/* ---------- Interface: MeemStoreCallBack --------------------------------- */

	public void meemStored(MeemPath meemPath) throws RemoteException {

		logger.log(Level.INFO, "# meemStored(" + meemPath + ")");

		meemStoreClient.meemStored(meemPath);
	}

	public void meemDestroyed(MeemPath meemPath) throws RemoteException {

		logger.log(Level.INFO, "# meemDestroyed(" + meemPath + ")");

		meemStoreClient.meemDestroyed(meemPath);
	}

	public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) throws RemoteException {

		logger.log(Level.INFO, "# meemContentChanged(" + meemPath + ")");

		meemContentClient.meemContentChanged(meemPath, meemContent);
	}

	public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition) throws RemoteException {

		logger.log(Level.INFO, "# meemDefinitionChanged(" + meemPath + ")");

		meemDefinitionClient.meemDefinitionChanged(meemPath, meemDefinition);
	}

	/* ---------- ContentProvider: MeemContentClient --------------------------- */

	private final class MeemContentClientContentProvider implements ContentProvider<MeemContentClient> {

		public void sendContent(MeemContentClient client, Filter filter) {

			try {
				logger.log(Level.INFO, "# getMeemContents(" + filter + ")");

				Map<MeemPath, MeemContent> meemContents = meemStoreCallForward.getMeemContent(filter);

				for (Entry<MeemPath, MeemContent> contentEntry : meemContents.entrySet()) {
					MeemPath meemPath = contentEntry.getKey();
					MeemContent meemContent = contentEntry.getValue();
					client.meemContentChanged(meemPath, meemContent);
				}
			}
			catch (Exception exception) {
				logger.log(Level.WARNING, "# sendContent(): " + exception);
			}
		}
	}

	/* ---------- ContentProvider: MeemDefinitionClient ------------------------ */

	private final class MeemDefinitionClientContentProvider implements ContentProvider<MeemDefinitionClient> {

		public void sendContent(MeemDefinitionClient client, Filter filter) {

			try {
				logger.log(Level.INFO, "# getMeemDefinitions(" + filter + ")");

				Map<MeemPath, MeemDefinition> meemDefinitions = meemStoreCallForward.getMeemDefinition(filter);

				for (Entry<MeemPath, MeemDefinition> defEntry : meemDefinitions.entrySet()) {
					MeemPath meemPath = defEntry.getKey();
					MeemDefinition meemDefinition = defEntry.getValue();
					client.meemDefinitionChanged(meemPath, meemDefinition);
				}
			}
			catch (Exception exception) {
				logger.log(Level.WARNING, "# sendContent(): " + exception);
			}
		}
	}
}
