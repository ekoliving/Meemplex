/*
 * @(#)LaunchEclipsePlugin.java
 * 
 * Copyright 2003 by EkoLiving Pty Ltd. All Rights Reserved.
 * 
 * This software is the proprietary information of EkoLiving Pty Ltd. Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.plugin;

import java.io.IOException;


//
//
//import java.util.logging.Level;
import java.util.logging.Logger;
import org.openmaji.implementation.server.utility.PropertiesLoader;
import org.openmaji.system.genesis.Genesis;


public class LaunchEclipsePlugin {

	private static boolean majiStarted = false;
	
//	private static final Logger logger = Logger.getAnonymousLogger();
	
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