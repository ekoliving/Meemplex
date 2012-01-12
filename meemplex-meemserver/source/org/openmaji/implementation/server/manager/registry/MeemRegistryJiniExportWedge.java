/*
 * @(#)MeemRegistryJiniExportWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Ensure that leasing with the Jini Lookup Service is working correctly.
 * - Don't forget to use Export.unexport(), when deregistering the RemoteMeem.
 */

package org.openmaji.implementation.server.manager.registry;

import java.io.IOException;
import java.util.*;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meem.*;
import org.openmaji.implementation.server.meem.wedge.remote.SmartProxyMeem;
import org.openmaji.implementation.server.nursery.jini.lookup.TimeStampEntry;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.wedge.remote.RemoteMeem;
import org.openmaji.system.meem.wedge.remote.RemoteMeemClient;
import org.openmaji.utility.CollectionUtility;
import org.swzoo.log2.core.*;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.MeemPath
 */

public class MeemRegistryJiniExportWedge
  extends    MeemRegistryWedge
  implements RemoteMeemClient {

  private static final String MEEM_REGISTRY_JINI_EXPORT_WEDGE_NAME =
    "org.openmaji.implementation.server.manager.registry.MeemRegistryJiniExportWedge";

  private static final String REMOTE_MEEM_EXPORTER_NAME = "remoteMeemExporter";

  private Configuration configuration = null;

  private LookupDiscoveryManager lookupDiscoveryManager = null;

  private LeaseRenewalManager leaseRenewalManager = null;

  /**
   * Collection of JoinManagers for registered SmartProxyMeems
   */

  private HashMap joinManagers = CollectionUtility.createHashMap();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	// if we are shutting down, then we need to clean up after ourselves
	public void conclude() {
		Set meems;
		synchronized(exportedMeems) {
			meems = new HashSet(exportedMeems);
		}

		Iterator iter = meems.iterator();
		
		while (iter.hasNext()) {
			deregisterMeem((Meem)iter.next());
		}
	}

  private void initialize() {
    String majitekDirectory =
      System.getProperty(Common.PROPERTY_MAJI_HOME);

    if (majitekDirectory == null) {
      throw new RuntimeException(
        "Empty Majitek directory property: " + Common.PROPERTY_MAJI_HOME
      );
    }

    try {
      configuration = ConfigurationProvider.getInstance(
        new String[] {
          majitekDirectory +
          System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE)
        }
      );
    }
    catch (ConfigurationException configurationException) {
      throw new RuntimeException(
        "ConfigurationProviderException:" + configurationException
      );
    }

    if (lookupDiscoveryManager == null) {
      try {
        lookupDiscoveryManager = new LookupDiscoveryManager(
          new String[] { MeemSpace.getIdentifier() },
          null,  // LookupLocator[]
          null   // DiscoveryListener
        );
      }
      catch (IOException ioException) {
        throw new RuntimeException("LookupDiscoveryManager: IOException");
      }
    }

    if (leaseRenewalManager == null) {
      leaseRenewalManager = new LeaseRenewalManager();

//    (LeaseRenewalManager) configuration.getEntry(
//      "net.jini.lease.LeaseRenewalManager",  // Component
//      "leaseRenewalManager",                 // Name
//      LeaseRenewalManager.class              // Class
//    );
    }
  }

	//
	// a set indicating the meems we have exported so that the import wedge can tell if
	// a proxy it has been given is actually one for a meem in the local VM.
	//
	private static final Set exports = Collections.synchronizedSet(new HashSet());
	private static final Set exportedMeems = Collections.synchronizedSet(new HashSet());
	
	static boolean isExported(
		MeemPath	meemPath)
	{
		return exports.contains(meemPath);
	}
	
  public void registerMeem(
    Meem meem) {

    super.registerMeem(meem);

	if (exports.contains(meem.getMeemPath()))
	{
		LogTools.error(logger, "Attempt to export already exported meem: " + meem);
		return;
	}
	
    if (lookupDiscoveryManager == null) initialize();

    Reference remoteMeemClientReference =
      Reference.spi.create(
        "remoteMeemClientFacet", meemCore.getTarget("remoteMeemClient"), true
      );

    meem.addOutboundReference(remoteMeemClientReference, true);
  }

  public synchronized void remoteMeemChanged(
    Meem        meem,
    RemoteMeem  remoteMeem,
    FacetItem[] facetItems) {
    	
    	if (facetItems.length < 2) {
    		return;
    	}

    MeemPath meemPath = meem.getMeemPath();

	exports.add(meemPath);
	exportedMeems.add(meem);
	
    try {
      Exporter remoteMeemExporter = (Exporter) configuration.getEntry(
        MEEM_REGISTRY_JINI_EXPORT_WEDGE_NAME,  // component
        REMOTE_MEEM_EXPORTER_NAME,             // name
        Exporter.class                         // class
      );

      remoteMeem = (RemoteMeem) remoteMeemExporter.export(remoteMeem);

      Meem smartProxyMeem =
        new SmartProxyMeem(remoteMeem, meemPath).getSmartProxyMeem();

      ServiceID serviceID = MeemRegistryJiniUtility.createServiceID(meemPath);

//    leaseRenewalManager.renewUntil(
//      serviceRegistration.getLease(),
//      Lease.ANY,
//      null        //LeaseListener
//    );

      Entry[] entries = null;

      if (facetItems != null) {
        entries = new Entry[facetItems.length + 1];

        for (int index = 0; index < facetItems.length; index ++) {
          entries[index] = new FacetInformation(facetItems[index]);
        }
        
        entries[facetItems.length] = new TimeStampEntry();
      }

      try {
        JoinManager joinManager = new JoinManager(
          smartProxyMeem,             // Object
          entries,                    // Entry[] AttrSets
          serviceID,                  // ServiceID
          lookupDiscoveryManager,     // LookupDiscoveryManager
          leaseRenewalManager
        );

        if (joinManagers.containsKey(meemPath) == false) {
          joinManagers.put(meemPath, joinManager);
        }
      }
      catch (IOException ioException) {
        throw new RuntimeException("JoinManager: IOException: " + ioException);
      }
      catch (IllegalArgumentException illegalArgumentException) {
        throw new RuntimeException(
          "JoinManager: " + illegalArgumentException +
          ", remoteMeem = " + remoteMeem
        );
      }
    }
    catch (Exception exception) {
      LogTools.error(logger, "Exporting RemoteMeem: " + exception);
    }

  }

  public void deregisterMeem(
    Meem meem) {

    super.deregisterMeem(meem);

    MeemPath meemPath = meem.getMeemPath();

    JoinManager joinManager = (JoinManager) joinManagers.get(meemPath);

    if (joinManager != null) {
      joinManager.terminate();

      joinManagers.remove(meemPath);

      // Supposed to use remoteMeemExporter.unexport() when deregistering remoteMeem ?
    }

		exports.remove(meemPath);
		exportedMeems.remove(meem);

//  leaseRenewalManager.cancel(serviceRegistration.getLease());
  }

  /* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {this.getClass(), JiniServiceExportWedge.class }
      );
    }
    
    return(meemDefinition);
  }
/* ---------- Logging fields ----------------------------------------------- */

  /**
   * Create the per-class Software Zoo Logging V2 reference.
   */

  private static final Logger logger = LogFactory.getLogger();
}
