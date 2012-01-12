/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.logic;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.common.Binary;
import org.openmaji.diagnostic.Debug;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * Sends a true/false pulse to 2 different binary outputs.  If the input to this wedge is true,
 * then a pulse is sent to the binaryOnOutput, otherwise if false, a pulse is sent to the binaryOffOutput.
 */

public class BinaryPulseWedge implements Wedge {
	private static final Logger logger = LogFactory.getLogger();

	/* ---------------- facets ------------------- */
	
	public Binary binaryOffOutput;
	
	public Binary binaryOnOutput;

	/* ------------------- conduits ---------------- */
	
	public Binary binaryControlConduit = new BinaryControlConduit();

	public Binary binaryStateConduit;

	public ThreadManager threadManagerConduit;

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public Debug debugConduit = new MyDebugConduit();

	
	
	public transient ConfigurationSpecification truePulseDurationSpecification = new ConfigurationSpecification("True pulse duration in milliseconds", Integer.class, LifeCycleState.READY);

	public transient ConfigurationSpecification falsePulseDurationSpecification = new ConfigurationSpecification("False pulse duration in milliseconds", Integer.class, LifeCycleState.READY);

	/* -------------- persisted properies ------------------- */
	
	public int truePulseDuration = DEFAULT_TRUE_PULSE_DURATION_MILLIS;

	public int falsePulseDuration = DEFAULT_FALSE_PULSE_DURATION_MILLIS;

	private int debugLevel;

	
	/* ---------- configuration getters/setters ------------------------------- */

	public void setTruePulseDuration(Integer value) {
		truePulseDuration = value.intValue();
	}

	public int getTruePulseDuration() {
		return truePulseDuration;
	}

	public void setFalsePulseDuration(Integer value) {
		falsePulseDuration = value.intValue();
	}

	public int getFalsePulseDuration() {
		return falsePulseDuration;
	}

	/* ---------- BinaryControlConduit ---------------------------------------- */

	class BinaryControlConduit implements Binary {
		public synchronized void valueChanged(boolean value) {
			binaryStateConduit.valueChanged(value);
			if (debugLevel > 0) {
				LogTools.info(logger, "Start of pulse, sent a '" + value + "' value");
			}

			binaryOnOutput.valueChanged(value);
			binaryOffOutput.valueChanged(!value);
			if (value) {
				Thread thread = new TruePulseThread();
				threadManagerConduit.queue(thread, System.currentTimeMillis() + truePulseDuration);
			}
			else {
				Thread thread = new FalsePulseThread();
				threadManagerConduit.queue(thread, System.currentTimeMillis() + falsePulseDuration);
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MyDebugConduit implements Debug {
		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}

	/* ------------------------------------------------------------------------ */

	private class TruePulseThread extends Thread {
		public void run() {
			binaryOnOutput.valueChanged(false);
			if (debugLevel > 0) {
				LogTools.info(logger, "End of pulse, sent a 'false' value to binaryOnOutput");
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class FalsePulseThread extends Thread {
		public void run() {
			binaryOffOutput.valueChanged(false);
			if (debugLevel > 0) {
				LogTools.info(logger, "End of pulse, sent a 'false' value to binaryOffOutput");
			}
		}
	}
	
	
	public static final int DEFAULT_TRUE_PULSE_DURATION_MILLIS = 1000;

	public static final int DEFAULT_FALSE_PULSE_DURATION_MILLIS = 1000;


}
