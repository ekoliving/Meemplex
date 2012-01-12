/*
 * @(#)GenesisImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Pass LifeCycleManager MeemPath Property via Configuration in-bound Facet.
 *
 * - Change the essentialLCM.start() method to ...
 *   "LifeCycle.lifeCycleStateChanged(READY)"
 *
 * - Replace "IdentityManagerWedge.getSubject()" with an IdentityManagerHelper.
 *
 * - Ensure that the "hyperSpaceRootUID" isn't hard-coded.
 */

package org.openmaji.implementation.server.genesis;

import java.rmi.RMISecurityManager;
import java.security.PrivilegedAction;
import java.util.*;

import javax.security.auth.Subject;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.classloader.Launch;
import org.openmaji.implementation.server.manager.thread.PoolingThreadManagerWedge;
import org.openmaji.implementation.server.nursery.jini.JiniStarter;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;

import org.openmaji.meem.definition.*;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.genesis.Genesis;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationEntry;
import org.openmaji.system.spi.SpecificationType;
import org.swzoo.log2.core.*;

/**
 * <p>
 * Genesis is the environment independent means of bootstrapping a
 * MeemServer from nothing to being able to create and activate Meems.
 * </p>
 * <p>
 * The following System properies are used by this class:
 * </p>
 * <ul>
 *   <li>Genesis.SHUTDOWN_HOOK_PROPERTIES_KEY (optional) - Enables the shutdown hook for clean termination</li>
 * </ul>
 * <p>
 * Note: Implementation thread safe = Single threaded (2003-02-24)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.system.genesis.Genesis
 * @see org.openmaji.system.manager.lifecycle.LifeCycleManager
 * @see org.openmaji.system.meem.definition.DefinitionFactory
 * @see org.openmaji.implementation.server.genesis.LaunchMeemServer
 */

public class GenesisImpl implements Genesis {

	/**
	 * An optional System property that indicates whether the Java Runtime
	 * Shutdown Hook should be enabled.  By default, it is disabled.
	 */

	public static final String SHUTDOWN_HOOK_PROPERTIES_KEY = "org.openmaji.server.genesis.shutdownHook";
	
	public static final String PROPERTY_BUILD_DATE = "org.openmaji.implementation.buildDate";

	public static final String PROPERTY_TRUSTSTORE = "org.openmaji.security.TrustStore";
	
	public static final String PROPERTY_TRUSTSTORE_PASSWORD = "org.openmaji.security.TrustStorePasswd";


	/**
	 * Indicates whether the Java Runtime Shutdown Hook should be enabled. 
	 */

	public static boolean shutdownHookEnabled = false;

	/**
	 * Creates and registers a set of essential Meems via the LifeCycleManager.
	 * This runs with the default Subject obtained from the IdentityManager.
	 *
	 * @exception RuntimeException         Incorrect Genesis profile specified
	 * @exception IllegalArgumentException Problem loading essential Meem
	 * @exception RuntimeException         Problem instantiating essential Meem
	 */

	public synchronized void bigBang() throws IllegalArgumentException {

		Subject.doAsPrivileged(MeemCoreRootAuthority.getSubject(), new PrivilegedAction<Object>() {
			public Object run() {
				bigBangInternal();
				return (null);
			}
		}, null);
	}

	/**
	 * Creates and registers a set of essential Meems via the LifeCycleManager.
	 *
	 * @exception RuntimeException         Incorrect Genesis profile specified
	 * @exception IllegalArgumentException Problem loading essential Meem
	 * @exception RuntimeException         Problem instantiating essential Meem
	 */

	private void bigBangInternal() throws IllegalArgumentException {

		LogTools.info(logger, Common.getIdentification() + " Initializing ...");

		LogTools.info(logger, "MeemSpace identifier: " + MeemSpace.getIdentifier());

		String meemServerName = MeemServer.spi.getName();

		if (meemServerName == null) {
			throw new RuntimeException("Empty meemServerName property: " + MeemServer.PROPERTY_MEEMSERVER_NAME);
		}

		LogTools.info(logger, "MeemServer name: " + meemServerName);

		// --------------------------------------
		// Property for specifying the build date

		String buildDateString = System.getProperty(PROPERTY_BUILD_DATE);

		if (buildDateString != null) {
			LogTools.info(logger, "Build date: " + buildDateString);
		}

/*
 * Transition to using Java Web Start ... need to ensure that classes loaded
 * using the MajiClassLoader don't get security exceptions.  Until then, use
 * a gross hack and disable the SecurityManager.
 * 
 * Unofficial Java Web Start/JNLP FAQ
 * Q: Can I use my own custom ClassLoader?
 * http://lopica.sourceforge.net/faq.html#customcl
 */
    
		if (Launch.isUsingJNLP() == false) {
		  System.setSecurityManager(new RMISecurityManager());
		}

		// set up trusted certificates
		System.setProperty(
				"javax.net.ssl.trustStore", 
				System.getProperty(Common.PROPERTY_MAJI_HOME) + "/" + System.getProperty(PROPERTY_TRUSTSTORE)
			);
		System.setProperty(
				"javax.net.ssl.trustStorePassword", 
				System.getProperty(PROPERTY_TRUSTSTORE_PASSWORD)
			);

		// --------------------------------------------------------------
		// Property for indicating whether the Java Runtime Shutdown Hook
		// should be installed 

		String shutdownHookEnabledProperties = System.getProperty(SHUTDOWN_HOOK_PROPERTIES_KEY);

		if (shutdownHookEnabledProperties != null && shutdownHookEnabledProperties.equalsIgnoreCase("true")) {
			shutdownHookEnabled = true;

			Runtime.getRuntime().addShutdownHook(ShutdownHelper.getShutdownHelper());
		}

		// ------------------------------------------------------
		// Check that networking for this computer is ok for Jini

		JiniStarter jiniStarter = new JiniStarter();
		if (jiniStarter.validateHostAndPort() == false) {
			throw new RuntimeException("Networking not suitable for Jini");
		}

		// -----------------------------------------------------
		// Create the list of manditory essential Meems.
		// Always add new essential Meems using the MajiServerInitializer.
		// All EssentialMeem Specifications MUST have spi.getIdentifier(). 

		LinkedHashSet<String> essentialMeemIdentifiers = new LinkedHashSet<String>();

		addEssentialMeemIdentifiers(essentialMeemIdentifiers, SpecificationType.ESSENTIAL_MEEM, "Essential");

		// -------------------------------------------------------------------
		// Construct list of essential Meems based on the MeemServer's profile

		String profileProperty = System.getProperties().getProperty(PROPERTY_GENESIS_PROFILE, DEFAULT_GENESIS_PROFILE);

		StringTokenizer stringTokenizer = new StringTokenizer(profileProperty, ":");

		if (stringTokenizer.hasMoreTokens() == false) {
			throw new RuntimeException("Empty Genesis profile: " + PROPERTY_GENESIS_PROFILE);
		}

		String profile = stringTokenizer.nextToken();

		LogTools.info(logger, "Genesis profile: " + profile);

		// -------------------------------------------------------------------
		// Profile: Core essential Meems. Minimal Maji application environment

		if (profile.equals("core")) {
			// Nothing more to do
		}

		// --------------------------------------------------------
		// Profile: Custom choice of essential Meems.  Smorgasboard

		else if (profile.equals("custom")) {
			while (stringTokenizer.hasMoreTokens()) {
				String essentialMeemIdentifier = stringTokenizer.nextToken(":,");

				essentialMeemIdentifiers.add(essentialMeemIdentifier);
			}
		}

		// ---------------------------------------------------------------
		// Profile: All essential Meems.  Complete Maji system environment

		else if (profile.equals("all")) {
			addEssentialMeemIdentifiers(essentialMeemIdentifiers, SpecificationType.SYSTEM_MEEM, "System");
		}

		// --------
		// ! Oops !

		else {
			throw new RuntimeException("Unknown Genesis profile: " + profile);
		}

		// -----------------------------------------------------------------
		// Prepare to bootstrap MeemServer via the EssentialLifeCycleManager

		EssentialLifeCycleManager essentialLifeCycleManager = EssentialLifeCycleManager.spi.create();

		essentialLifeCycleManager.bootstrap();

		// --------------------------------------------------------
		// Create all essential Meems specified for this MeemServer

		MeemDefinitionFactory meemDefinitionFactory = MeemDefinitionFactory.spi.create();

		for (String meemTypeIdentifier : essentialMeemIdentifiers) {
			LogTools.info(logger, "Create essential Meem: " + meemTypeIdentifier);

			MeemDefinition meemDefinition = meemDefinitionFactory.createMeemDefinition(meemTypeIdentifier);

			essentialLifeCycleManager.createEssentialMeem(meemDefinition, LifeCycleState.READY);
		}

		// ----------------------------------------------------
		// Complete EssentialLifeCycleManager bootstrap process

		//-ag- Make this PoolingThreadManagerWedge a singleton and "startup" in the constructor
		PoolingThreadManagerWedge.startup();

		LogTools.info(logger, "Starting EssentialLifeCycleManager");
		essentialLifeCycleManager.start();

		LogTools.info(logger, Common.getIdentification() + " ... initialized !");
	}

	/**
	 * 
	 * @param meemIdentifiers
	 * @param specificationType
	 * @param meemTypeName
	 */
	private void addEssentialMeemIdentifiers(Collection<String> meemIdentifiers, SpecificationType specificationType, String meemTypeName) {

		MajiSystemProvider majiSystemProvider = MajiSystemProvider.systemProvider();

		for (Class<?> meemSpecification : majiSystemProvider.getSpecifications(specificationType)) {
			SpecificationEntry specificationEntry = majiSystemProvider.getSpecificationEntry(meemSpecification);

			String meemIdentifier = specificationEntry.getIdentifier();

			if (meemIdentifier == null || meemIdentifier.length() == 0) {
				throw new RuntimeException(meemTypeName + " Meem Specification doesn't have an identifier: " + meemSpecification);
			}

			meemIdentifiers.add(meemIdentifier);
		}
	}

	/* (non-Javadoc)
	 * @see org.openmaji.system.genesis.Genesis#getSubject()
	 */
	public Subject getSubject() {
		return MeemCoreRootAuthority.getSubject();
	}

	private String getSecurityHome() {
			String prefix = "";

			String majiHome = System.getProperty(Common.PROPERTY_MAJI_HOME);
			if (majiHome != null) {
				prefix = majiHome + "/";
				String meemServer = MeemServer.spi.getName();
				if (meemServer != null) {
					prefix += "servers/" + meemServer + "/";
				}
			}
			return prefix + "security/";
	}
	
	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = LogFactory.getLogger();
}
