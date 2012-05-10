/*
 * Created on 9/12/2004
 */
package org.openmaji.implementation.server.security.auth;


import java.io.IOException;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.manager.user.AuthenticatorStatus;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;

/**
 * A Wedge that receives a remote proxy for the Maji Authenticator Jini Service.
 * 
 * @author Warren Bloomer
 *
 */
public class AuthenticatorLookupWedge implements Wedge,  MeemDefinitionProvider, ServiceDiscoveryListener, MajiConstants {

	private static final Logger logger = LogFactory.getLogger();

	public  AuthenticatorStatus authenticatorStatus;
	public final ContentProvider authenticatorStatusProvider = 
		new ContentProvider() {
			public void sendContent(Object target, Filter filter)
				throws ContentException 
			{
				if (authenticator == null) {
					((AuthenticatorStatus)target).authenticatorLost();					
				}
				else {
					((AuthenticatorStatus)target).authenticatorLocated();
				}
			}
		};
	
	private LookupDiscoveryManager  lookupDiscoveryManager  = null;
	private ServiceDiscoveryManager serviceDiscoveryManager = null;
	private LookupCache             lookupCache             = null;

	private static Class<?>[] serviceInterfaces = new Class[] { Authenticator.class };

	private static ServiceTemplate serviceTemplate = new ServiceTemplate(null, serviceInterfaces, null);

	/* ------------------- conduits ------------------ */
	
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);


	private static Authenticator authenticator = null;
	private static boolean local = false;		// whether the authenticator is local

	
	public AuthenticatorLookupWedge() {
		// listen for local authenticator
		AuthenticatorExporterWedge.addListener(new AuthenticatorListenerImpl());		
	}
	
	public static Authenticator getAuthenticator() {
		return authenticator;
	}

	/* ------------------ Lifecycle methods ---------------------- */
	
	public void commence() {
		startLookup();
	}
	
	public void conclude() {
		stopLookup();

		authenticatorStatus.authenticatorLost();
		authenticator = null;
	}


	/* -------------------------- private methods ------------------- */
	
	/**
	 * 
	 */
	private void startLookup() {

		if (authenticator != null) {
			return;
		}
		
		// try to get local authenticator first
		authenticator = AuthenticatorExporterWedge.getAuthenticatorService();
		if (authenticator != null) {
			local = true;
			authenticatorStatus.authenticatorLocated();
			LogTools.info(logger, "Located local Maji authenticator.");
			return;
		}

		initialize();

		// Jini Service lookup 
		if (lookupCache == null) {
			if (serviceDiscoveryManager != null) {
				try {
					lookupCache = 
						serviceDiscoveryManager.createLookupCache(
								serviceTemplate, 
								new MyFilter(), 
								this
							);
				} 
				catch (Exception exception) {
					throw new RuntimeException("LookupCache: Exception: " + exception);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void stopLookup() {
		
		if (lookupCache != null) {
			lookupCache.terminate();
			lookupCache = null;
		}
		
		if (serviceDiscoveryManager != null) {
			serviceDiscoveryManager.terminate();
			serviceDiscoveryManager = null;
		}
		
		if (lookupDiscoveryManager != null) {			
			lookupDiscoveryManager.terminate();
			lookupDiscoveryManager = null;
		}
		
		authenticator = null;
	}

	/**
	 *
	 */
	private void initialize() {
		// Jini initialization
		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);

		if (majitekDirectory == null) {
			throw new RuntimeException("Empty Majitek directory property: " + Common.PROPERTY_MAJI_HOME);
		}
		
		Configuration configuration = null;
		try {
			configuration =
				ConfigurationProvider.getInstance(
					new String[] { majitekDirectory + System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE) }
				);
		} 
		catch (ConfigurationException configurationException) {
			throw new RuntimeException("ConfigurationProviderException:" + configurationException);
		}

		if (lookupDiscoveryManager == null) {
			try {
				lookupDiscoveryManager = 
					new LookupDiscoveryManager(
							new String[] { MeemSpace.getIdentifier() }, 	// groups
							null, 											// LookupLocator[]
							null 											// DiscoveryListener
				);
			} 
			catch (IOException ioException) {
				throw new RuntimeException("LookupDiscoveryManager: IOException: " + ioException);
			}
		}

		if (serviceDiscoveryManager == null) {
			if (lookupDiscoveryManager != null) {
				try {
					serviceDiscoveryManager = new ServiceDiscoveryManager(lookupDiscoveryManager, new LeaseRenewalManager(), configuration);
				} 
				catch (IOException ioException) {
					throw new RuntimeException("ServiceDiscoveryManager: IOException: " + ioException);
				} 
				catch (ConfigurationException configurationException) {
					throw new RuntimeException("ConfigurationProviderException:" + configurationException);
				}
			}
		}
	}



	/* ----------------- SeriveDiscoveryListener interface ------------------------ */
	
	/**
	 * @see net.jini.lookup.ServiceDiscoveryListener#serviceAdded(net.jini.lookup.ServiceDiscoveryEvent)
	 */
	public void serviceAdded(ServiceDiscoveryEvent event) {
		
		if (authenticator != null && local) {
			// already have a local authenticator
			return;
		}

		ServiceItem serviceItem = event.getPostEventServiceItem();

		if (serviceItem.service instanceof Authenticator) {
			
			// TODO the Remote proxy will need to be "Prepared"

			authenticator = (Authenticator) serviceItem.service;
			authenticatorStatus.authenticatorLocated();

			LogTools.info(logger, "Located remote Maji authenticator.");
		}
	}

	/**
	 * @see net.jini.lookup.ServiceDiscoveryListener#serviceChanged(net.jini.lookup.ServiceDiscoveryEvent)
	 */
	public void serviceChanged(ServiceDiscoveryEvent event) {
	}
	
	/**
	 * @see net.jini.lookup.ServiceDiscoveryListener#serviceRemoved(net.jini.lookup.ServiceDiscoveryEvent)
	 */
	public void serviceRemoved(ServiceDiscoveryEvent event) {
		
		if (authenticator != null && local) {
			// already have a local authenticator
			return;
		}

		ServiceItem serviceItem = event.getPreEventServiceItem();

		if (serviceItem.service instanceof Authenticator) {
			Authenticator authenticator = (Authenticator) serviceItem.service;
			
			if ( authenticator.equals(AuthenticatorLookupWedge.authenticator) ) {
				AuthenticatorLookupWedge.authenticator = null;
			}
		}
	}

	
	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
					new Class[] { this.getClass() }
			);
		}
		return meemDefinition;
	}

	/* ---------- Nested class for SPI ----------------------------------------- */

	public static class spi {
		public static String getIdentifier() {
		  return("authenticatorLookup");
		};
	}

	  
	/* ------------------ --------------- */
	
//	private String getMeemSpaceIdentifier() {
//		return System.getProperty(PROPERTY_MEEMSPACE_IDENTIFIER);
//	}

	
	class MyFilter implements ServiceItemFilter {
		public boolean check(ServiceItem item) {
			return (item.service instanceof Authenticator);
		}
	}

	private class AuthenticatorListenerImpl implements AuthenticatorListener {
		public void authenticator(Authenticator authenticator) {

			LogTools.info(logger, "Located local Maji authenticator.");
			AuthenticatorLookupWedge.authenticator = authenticator;
			local = true;
			if (authenticatorStatus != null) {
				authenticatorStatus.authenticatorLocated();
			}
		}
	}
}
