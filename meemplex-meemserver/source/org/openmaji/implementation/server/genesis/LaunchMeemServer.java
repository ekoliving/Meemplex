/*
 * @(#)LaunchMeemServer.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Merge Launch properties from a URL.
 */

package org.openmaji.implementation.server.genesis;

import java.io.IOException;

import org.openmaji.implementation.server.utility.PropertiesLoader;
import org.openmaji.implementation.server.utility.PropertyUtility;
import org.openmaji.system.genesis.Genesis;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;
import org.swzoo.log2.util.ConfigurableLogFactory;

/**
 * <p>
 * This Launcher initiates a MeemServer within a stand-alone JVM. It acquires the MeemServer specific properties and after merging them with the system properties, then invokes
 * Genesis, which performs the MeemServer initialization.
 * </p>
 * 
 * <p>
 * This Launcher uses PropertiesLoader to load all the properties required to run this MeemServer. It requires the System property PropertiesLoader.PROPERTY_KEY to be set to a file
 * that contains all of the properties. This loader doesn't require any command line arguments.
 * </p>
 * 
 * <p>
 * The following System properies are used by this class:
 * </p>
 * 
 * <ul>
 * <li>PropertiesLoader.PROPERTY_KEY (required) - contains all properties</li>
 * <li>LaunchMeemServer.DUMP_PROPERTIES_KEY (optional) - display properties for diagnosis</li>
 * </ul>
 * 
 * <p>
 * Java properties can be used in an ad-hoc fashion throughout the code. However, the Maji platform encourages that as much configuration information as possible is stored as Meem
 * attributes in MeemStore or as Categories in HyperSpace. Thus minimizing the informal use of properties.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Single threaded (2003-02-25)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.system.genesis.Genesis
 * @see org.openmaji.implementation.server.genesis.GenesisImpl
 */

public class LaunchMeemServer {

	private static Logger logger = LogFactory.getLogger();

	/**
	 * An optional System property that indicates that the properties used by this Launcher be displayed.
	 */

	public static final String DUMP_PROPERTIES_KEY = "org.openmaji.server.genesis.dumpProperties";

	/**
	 * The null constructor assumes that the System property PropertiesLoader.PROPERTY_KEY has already been set.
	 */

	public LaunchMeemServer() {
	}

	/**
	 * This constructor allows you to specify the filename to use as the properties file for launching a MeemServer.
	 * 
	 * @param propertiesFilename
	 *            The name of the file containing a MeemServer's properties
	 */

	public LaunchMeemServer(String propertiesFilename) {
		System.setProperty(PropertiesLoader.PROPERTY_KEY, propertiesFilename);
	}

	/**
	 * Launch the MeemServer where the System property PropertiesLoader.PROPERTY_KEY specifies the file containing all of the properties needed by a MeemServer.
	 * 
	 * @throws IOException
	 */

	public void launch() throws IOException {

		PropertiesLoader.load();

		// Dump the system properties if

		String dumpProperties = System.getProperty(DUMP_PROPERTIES_KEY);
		if (dumpProperties != null) {
			PropertyUtility.dumpProperties(System.getProperties());
		}

		// Logger may have been affected by changes to system properties

		ConfigurableLogFactory.setConfiguration(System.getProperties(), null);
		logger = LogFactory.getLogger();

		// ----------------------------------
		// Complete MeemServer initialization establishing out initial subject

		Genesis genesis = Genesis.spi.create();
		genesis.bigBang();
	}

	/*
	 * Shutdown the MeemServer
	 */
	public void shutdown() {
		ShutdownHelper.shutdownMaji();
	}

	/**
	 * Command line execution Launcher for a stand-alone MeemServer. Command line documentation is at the class level (see above).
	 * 
	 * @param args
	 *            Command line arguments
	 */

	public static synchronized void main(String[] args) {
		boolean failure = false;

		if (args.length > 0) {
			LogTools.warn(logger, "main() - command line arguments ignored");
		}

		LaunchMeemServer launcher = new LaunchMeemServer();
		try {
			launcher.launch();
		}
		catch (IOException ex) {
			failure = true;

			System.err.println("Unable to launch MeemServer: " + ex.getMessage());
		}

		if (failure) {
			try {
				Thread.sleep(20000L);
			}
			catch (InterruptedException interruptedException) {
			}

			System.exit(1);
		}
	}
}