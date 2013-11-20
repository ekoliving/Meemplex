/*
 * @(#)JiniMeemRegistryExportWedge.java
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

import java.io.IOException;
import java.util.*;

import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;
import org.openmaji.implementation.server.meem.FacetInformation;
import org.openmaji.implementation.server.meem.wedge.remote.SmartProxyMeem;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.registry.MeemRegistry;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.remote.RemoteMeem;
import org.openmaji.system.meem.wedge.remote.RemoteMeemClient;
import org.openmaji.utility.CollectionUtility;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class JiniMeemRegistryExportWedge implements Wedge, MeemRegistry {
	
	public MeemCore meemCore;
	
	public RemoteMeemClient remoteMeemClient = new RemoteMeemClientAdapter();

	private LookupDiscoveryManager lookupDiscoveryManager = null;

	//private Configuration configuration = null;

	private final Set exports = Collections.synchronizedSet(new HashSet());
	private final Set exportedMeems = Collections.synchronizedSet(new HashSet());

	private HashMap joinManagers = CollectionUtility.createHashMap();
	private LeaseRenewalManager leaseRenewalManager = null;

	public void registerMeem(Meem meem) {
		if (exports.contains(meem.getMeemPath())) {
			logger.log(Level.WARNING, "Attempt to export already exported meem: " + meem);
			return;
		}

		if (lookupDiscoveryManager == null) {
			initialize();
		}

		Reference remoteMeemClientReference =
			Reference.spi.create("remoteMeemClientFacet", meemCore.getLimitedTargetFor(remoteMeemClient, RemoteMeemClient.class), true);

		meem.addOutboundReference(remoteMeemClientReference, true);
	}

	public void deregisterMeem(Meem meem) {
		MeemPath meemPath = meem.getMeemPath();

		JoinManager joinManager = (JoinManager) joinManagers.get(meemPath);

		if (joinManager != null) {
			joinManager.terminate();

			joinManagers.remove(meemPath);

			// Supposed to use remoteMeemExporter.unexport() when deregistering remoteMeem ?
		}

		exports.remove(meemPath);
		exportedMeems.remove(meem);

		//leaseRenewalManager.cancel(serviceRegistration.getLease());
	}

	/**
	 * 
	 */
	private void initialize() {
		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);

		if (majitekDirectory == null) {
			throw new RuntimeException("Empty Majitek directory property: " + Common.PROPERTY_MAJI_HOME);
		}

		try {
			//configuration =
				ConfigurationProvider.getInstance(
					new String[] { majitekDirectory + System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE)});
		} catch (ConfigurationException configurationException) {
			throw new RuntimeException("ConfigurationProviderException:" + configurationException);
		}

		if (lookupDiscoveryManager == null) {
			try {
				lookupDiscoveryManager = new LookupDiscoveryManager(
						new String[] { MeemSpace.getIdentifier() }, 
						null, // LookupLocator[]
						null // DiscoveryListener
				);
			} catch (IOException ioException) {
				throw new RuntimeException("LookupDiscoveryManager: IOException");
			}
		}

		if (leaseRenewalManager == null) {
			leaseRenewalManager = new LeaseRenewalManager();

			//	  (LeaseRenewalManager) configuration.getEntry(
			//		"net.jini.lease.LeaseRenewalManager",  // Component
			//		"leaseRenewalManager",                 // Name
			//		LeaseRenewalManager.class              // Class
			//	  );
		}
	}


	public class RemoteMeemClientAdapter implements RemoteMeemClient {
		public synchronized void remoteMeemChanged(Meem meem, RemoteMeem remoteMeem, FacetItem[] facetItems) {

			MeemPath meemPath = meem.getMeemPath();

			exports.add(meemPath);
			exportedMeems.add(meem);

			try {
				
				remoteMeem = (RemoteMeem) ExporterHelper.export(remoteMeem);

				Meem smartProxyMeem = new SmartProxyMeem(remoteMeem, meemPath).getSmartProxyMeem();

				ServiceID serviceID = MeemRegistryJiniUtility.createServiceID(meemPath);

				//	leaseRenewalManager.renewUntil(
				//	  serviceRegistration.getLease(),
				//	  Lease.ANY,
				//	  null        //LeaseListener
				//	);

				Entry[] entries = null;

				if (facetItems != null) {
					entries = new FacetInformation[facetItems.length];

					for (int index = 0; index < facetItems.length; index++) {
						entries[index] = new FacetInformation(facetItems[index]);
					}
				}

				try {
						JoinManager joinManager = new JoinManager(
							smartProxyMeem, // Object
							entries, // Entry[] AttrSets
							serviceID, // ServiceID
							lookupDiscoveryManager, // LookupDiscoveryManager
							leaseRenewalManager);

					if (joinManagers.containsKey(meemPath) == false) {
						joinManagers.put(meemPath, joinManager);
					}
				} catch (IOException ioException) {
					throw new RuntimeException("JoinManager: IOException: " + ioException);
				} catch (IllegalArgumentException illegalArgumentException) {
					throw new RuntimeException("JoinManager: " + illegalArgumentException + ", remoteMeem = " + remoteMeem);
				}
			} catch (Exception exception) {
				logger.log(Level.WARNING, "Exporting RemoteMeem: " + exception);
			}

		}
	}

	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = Logger.getAnonymousLogger();
}
