/*
 * @(#)AuthorizationExporterWedge.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.security.auth;

import java.io.*;
import java.rmi.Remote;
import java.util.HashSet;

import net.jini.config.*;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.id.*;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;


import org.openmaji.meem.*;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.system.meem.core.MeemCore;
import org.swzoo.log2.core.*;


/**
 * 
 * @author Warren Bloomer
 *
 */
public class AuthenticatorExporterWedge
	implements Wedge, MeemDefinitionProvider, MajiConstants
{

	private static final Logger logger = LogFactory.getLogger();
	
	
	//private static final String AUTHENTICATOR_EXPORTER_COMPONENT = "org.openmaji.implementation.server.security.auth.AuthenticatorService";
	private static final String AUTHENTICATOR_EXPORTER_COMPONENT = AuthenticatorService.class.getName();

	private static final String AUTHENTICATOR_EXPORTER_NAME = "authenticatorExporter";
	
	//private static final long timeout = 60000L;  // One minute
	

	/* --------------- meem core --------------------- */
	
	public MeemCore meemCore;

	
	/* ------------------- conduits ------------------ */
	
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);


	/* -------------- private members ----------------- */
	
	private Configuration configuration = null;

	private LookupDiscoveryManager lookupDiscoveryManager = null;

	private JoinManager joinManager;
	
	// The AuthenticatorService to export
	private static AuthenticatorService authenticatorService = null;
	private Remote authenticatorProxy = null;
	private Exporter authExporter = null;
	
	private static HashSet<AuthenticatorListener> listeners = new HashSet<AuthenticatorListener>();

	/**
	 * 
	 */
	protected synchronized static AuthenticatorService getAuthenticatorService() {
		return authenticatorService;
	}
	
	protected static void addListener(AuthenticatorListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
			if (authenticatorService != null) {
				listener.authenticator(authenticatorService);
			}
		}
	}
	
	/* ---------------------- Lifecycle methods --------------------- */

	public void commence() {
		
		if (authenticatorService == null) {
			authenticatorService = createAuthenticator();
			sendAuthenticator(authenticatorService);
		}

		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);

		// ---------- Jini initialization 
		
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
		  catch(IOException ioException) {
		    throw new RuntimeException("LookupDiscoveryManager: IOException");
		  }
		}
		
		
		// ---------- Jini Service register 
		
		Uuid uuid = UuidFactory.generate();
		
		try {
			authExporter = (Exporter) configuration.getEntry(
		  		AUTHENTICATOR_EXPORTER_COMPONENT,  // component
		  		AUTHENTICATOR_EXPORTER_NAME,        // name
				Exporter.class                      // class
			);

			authenticatorProxy = authExporter.export(authenticatorService);
		
			ServiceID serviceID = new ServiceID(
				uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()
			);

			LeaseRenewalManager leaseRenewalManager = new LeaseRenewalManager();

			try {
				joinManager = new JoinManager(
						authenticatorProxy,       // Object
						null,                       // Entry[] AttrSets
						serviceID,                  // ServiceID
						lookupDiscoveryManager,     // LookupDiscoveryManager
						leaseRenewalManager
				);
			}
			catch(IOException ioException) {
				throw new RuntimeException("JoinManager: IOException: " + ioException);
			}
			catch(IllegalArgumentException illegalArgumentException) {
				throw new RuntimeException(
						"JoinManager: AuthenticatorExporter: " + illegalArgumentException
				);
			}
		}
		catch (Exception exception) {
		  LogTools.error(logger, "Exporting AuthenticatorService: " + exception, exception);
		}

		LogTools.info(logger, "Jini Service registered: " + uuid);

	}

	public void conclude() {
		
		if (authExporter != null) {
			authExporter.unexport(false);
			authExporter = null;
		}
		
		if (authenticatorService != null) {
			authenticatorService.cleanup();
			authenticatorService = null;
			sendAuthenticator(authenticatorService);
		}

		authenticatorProxy = null;

		// -----------------------
		// Jini Service deregister
		//
		// Supposed to use meemStoreExporter.unexport() when deregistering ?
		
		if (joinManager != null) {
			joinManager.terminate();
			joinManager = null;
		}
	}

	
	/* ---------- MeemDefinitionProvider method(s) ---------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
	    if (meemDefinition == null) {
	      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
	        new Class[] { this.getClass() }
	      );
	    }
	    return meemDefinition;
	}

	
	/* ------------------ private methods ------------------------- */

	private static AuthenticatorService createAuthenticator() {
		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);

		if (majitekDirectory == null) {
			LogTools.info(logger, "Can not create authentication service.  Majitek directory not set.");
			return null;
		}
		
		String keyStoreName = System.getProperty(USER_KEYSTORE_NAME);
		if (keyStoreName == null) {
			LogTools.info(logger, "Can not create authentication service. Unable to find key store name.");
			return null;
		} 
		
		String keyStorePasswd = System.getProperty(USER_KEYSTORE_PASSWD);
		if (keyStorePasswd == null) {
			LogTools.info(logger, "Can not create authentication service. Unable to find key store password.");
			return null;
		} 

		// create new Authenticator Service
		return new AuthenticatorService(majitekDirectory + keyStoreName, keyStorePasswd);
	}

	/**
	 * Send the authenticator to listeners
	 * 
	 * @param authenticator
	 */
	private static void sendAuthenticator(Authenticator authenticator) {
		Object[] listenerArr; 
		synchronized(listeners) {
			listenerArr = listeners.toArray();
		}
		
		for (int i=0; i<listenerArr.length; i++) {
			((AuthenticatorListener)listenerArr[i]).authenticator(authenticator);
		}
	}

	/* ---------- Nested class for SPI ----------------------------- */

	public static class spi {

	    public static String getIdentifier() {
	      return("authenticatorExporter");
	    };
	}

}
