/*
 * @(#)ShutdownHelper.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.genesis;

import org.openmaji.implementation.server.manager.thread.PoolingThreadManagerWedge;

import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.server.helper.*;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Peter
 */
public class ShutdownHelper extends Thread {
	private static ShutdownHelper shutdownHelper = null;

	private static boolean shutdownInitiated = false;

	private ShutdownHelper() {
	}

	public static ShutdownHelper getShutdownHelper() {
		if (shutdownHelper == null)
			shutdownHelper = new ShutdownHelper();

		return shutdownHelper;
	}

	/**
	 * This is the "Thread hook" for Runtime.addShutdownHook().
	 */

	public void run() {
		System.err.println("Initiate shutdown ...");
		// logger.log(Level.INFO, "Initiate shutdown ...");

		if (ShutdownHelper.shutdownMaji()) {
			System.err.println("... shutdown succeeded");
			// logger.log(Level.INFO, "... shutdown succeeded");
		}
		else {
			System.err.println("... shutdown failed");
			// logger.log(Level.WARNING, "... shutdown failed");
		}
	}

	/**
	 * 
	 * @return true if maji shutdown proceeded.
	 */
	public static boolean shutdownMaji() {
		if (shutdownInitiated) {
			logger.log(Level.WARNING, "Shutdown already in progress");
			return false;
		}

		shutdownInitiated = true;

		LifeCycle lifeCycle = null;

		try {
			//System.err.println("getting essential LCM");

			Meem essentialLifeCycleManagerMeem = EssentialMeemHelper.getEssentialMeem(EssentialLifeCycleManager.spi.getIdentifier());

			//System.err.println("getting LCM reference");

			lifeCycle = ReferenceHelper.getTarget(essentialLifeCycleManagerMeem, "lifeCycle", LifeCycle.class);

			//System.err.println("lcm reference: " + lifeCycle);

			if (lifeCycle != null) {
				//System.err.println("changing lc state");
				lifeCycle.changeLifeCycleState(LifeCycleState.DORMANT);
			}
			else {
				shutdownInitiated = false;
			}
		}
		catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		finally {
			if (lifeCycle != null) {
				System.err.println("shutting down threading manager");
				PoolingThreadManagerWedge.shutdown();
			}
		}

		if (lifeCycle != null) {
			// Terminate JVM, but only if NOT running as part of the
			// Java Runtime Shutdown Hook
			if (GenesisImpl.shutdownHookEnabled == false)
				System.exit(0);
			return true;
		}
		else {
			logger.log(Level.WARNING, "Attempt by unprivileged user to shutdown.");
			return false;
		}
	}

	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = Logger.getAnonymousLogger();
}