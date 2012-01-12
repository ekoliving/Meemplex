/*
 * @(#)LaunchEclipsePlugin.java
 * 
 * Copyright 2003 by Majitek Limited. All Rights Reserved.
 * 
 * This software is the proprietary information of Majitek Limited. Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.plugin;

import java.io.IOException;


//import org.swzoo.log2.core.LogFactory;
//import org.swzoo.log2.core.LogTools;
//import org.swzoo.log2.core.Logger;
import org.openmaji.implementation.server.utility.PropertiesLoader;
import org.openmaji.system.genesis.Genesis;
import org.swzoo.log2.util.ConfigurableLogFactory;


public class LaunchEclipsePlugin {

	private static boolean majiStarted = false;
	
//	private static final Logger logger = LogFactory.getLogger();
	
	public static boolean startMaji() {
		if (majiStarted) {
			return true;
		}
		
		try {
			PropertiesLoader.load();
		}
		catch (IOException ex) {
			System.err.println("<init> - Unable to load properties: " + ex.getMessage());
		}

		// Logger may have been affected by changes to system properties

		ConfigurableLogFactory.setConfiguration(System.getProperties(), null);
		
		try {
			Genesis genesis = Genesis.spi.create();

			genesis.bigBang();
			
		}
		catch (Throwable t) {
			t.printStackTrace();
		}

		majiStarted = true;

		return true;
	}
}