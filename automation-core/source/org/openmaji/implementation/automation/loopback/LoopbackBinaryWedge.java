/*
 * @(#)LoopbackBinaryWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.common.Binary;
import org.openmaji.implementation.automation.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.BooleanConfigurationSpecification;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * The LoopbackBinaryWedge Wedge is a simple example of a pluggable Binary thing.
 * </p>
 * <p>
 * LoopbackBinaryWedge is a binaryControlConduit target that listens
 * for Binary method invocations and immediately passes on those method
 * invocations as a binarySourceConduit source.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-10-07)
 * </p>
 * @author  Andy Gelme
 * @author  Christos Kakris
 * @version 1.0
 * @see org.openmaji.meem.Wedge
 * @see org.openmaji.common.Binary
 */

public class LoopbackBinaryWedge implements Wedge
{
	private static final Logger logger = LogFactory.getLogger();

	public Binary binaryControlConduit = new BinaryControlConduit();
	
	public Binary binaryStateConduit = null;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	
	/**
	 * Boolean state maintained by this Wedge and persisted by the Maji framework
	 */

	public Boolean value = null;

	/**
	 * Whether to use persisted value, or wait for a fresh value before sending
	 * initial content.
	 */

	public boolean persistValue = true;

	/**
	 * Configuration specification for the "persistValue" property.
	 */
	public transient ConfigurationSpecification persistValueSpecification = new BooleanConfigurationSpecification("Whether to persist the Binary value");

	
/* --------- Configuration methods ----------------------------------------- */
	
	public void setPersistValue(Boolean persist) {
		this.persistValue = persist;
	}

	public Boolean getPersistValue() {
		return this.persistValue;
	}
	
/* --------- LifeCycle methods -------------------------------------------- */
	
	protected void commence() {
		if (persistValue) {
			// send persisted value down binaryStateConduit
			binaryStateConduit.valueChanged(this.value);
		}
	}

	
/* ---------- BinaryControlConduit ----------------------------------------- */

	private class BinaryControlConduit implements Binary
	{
		/**
		 * Respond to a value change by simply passing the change back to any
		 * Wedges that act as a binaryStateConduit target.
		 *
		 * @param value Changed boolean value
		 */
		
		public synchronized void valueChanged(boolean value)
		{
			if ( DebugFlag.TRACE ) {
				LogTools.trace(logger,20,"valueChanged() - invoked on BinaryControlConduit");
			}
			if (persistValue) {
				LoopbackBinaryWedge.this.value = value;
			}
			binaryStateConduit.valueChanged(value);
		}
	}
}
