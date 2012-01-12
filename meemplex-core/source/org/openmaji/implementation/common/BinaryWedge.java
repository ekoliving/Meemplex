/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider providing support for some simple Binary Filters, e.g only
 *   output "false to true" value changes transitions or vice-versa.
 *
 * - Consider generating an error, if the sendContent() target is not a Binary.
 *   Another approach is that the framework should pre-verify the target type.
 *
 * - Consider creating an optimized version, that only informs the Binary
 *   Meem clients, if the value really changed.  Is it possible to do this
 *   as a Filter (which would be much more flexible) ?
 */

package org.openmaji.implementation.common;

import org.openmaji.common.Binary;
import org.openmaji.implementation.common.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * The BinaryWedge Wedge is used to maintain the value of boolean type information.
 * For example, a switch that may be on or off.
 * </p>
 * <p>
 * BinaryWedge is intended to work in conjunction with another Wedge that
 * provides a specific implementation of some Binary thing.  Typically,
 * this other Wedge will implement a specific hardware protocol for
 * interacting with some Binary hardware device.
 * </p>
 * <p>
 * BinaryWedge provides a standardized client side interface for Binary things.
 * Using inter-Wedge Conduits, this Wedge can interoperate with other Binary
 * Wedges combined as part of the Meem.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-08-06)
 * </p>
 * @author  Andy Gelme
 * @author  Christos Kakris
 * @version 1.0
 * @see org.openmaji.common.Binary
 */

public class BinaryWedge implements Binary, Wedge {

	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Binary client (out-bound Facet)
	 */
	public Binary binaryClient;

	public final ContentProvider binaryClientProvider = new ContentProvider() {
		/**
		 * Send content to a Binary client that has just had its Reference added.
		 *
		 * @param target           Reference to the target Meem
		 * @param filter           No Filters are currently implemented
		 */
		public void sendContent(Object target, Filter filter) {
			if (DebugFlag.TRACE) {
				LogTools.trace(logger, 20, "sendContent() - invoked");
			}
			
			// only send value if one has been received on the binaryStateConduit, 
			// otherwise the value is invalid.
			if (value != null) {
				((Binary) target).valueChanged(value);
			}
		}
	};

	/**
	 * The conduit through which other Wedges in the Meem are notified of a
	 * request to change the value of the binary thing.  
	 */

	public Binary binaryControlConduit;

	/**
	 * The conduit through which state changes are received other Wedges in the Meem. 
	 */

	public Binary binaryStateConduit = new BinaryStateConduit();
	
	/**
	 * LifeCycleClient conduit.
	 */
	
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	/**
	 * Boolean state
	 */

	private Boolean value = null;


	/* ---------- Binary Facet method(s) --------------------------------------- */

	/**
	 * Respond to a value change by simply passing the change on to any Wedges
	 * that act as a binaryControlConduit target.
	 *
	 * @param value Changed boolean value
	 */

	public synchronized void valueChanged(boolean value) {

		if (DebugFlag.TRACE) {
			LogTools.trace(logger, 20, "valueChanged() - invoked on in-bound facet");
		}
		binaryControlConduit.valueChanged(value);
	}

	/* ---------- BinaryStateConduit ------------------------------------------- */

	/**
	 * This class handles incoming Binary state messages from other
	 * Wedges in the Meem.
	 * 
	 * @author Chris Kakris
	 */

	private final class BinaryStateConduit implements Binary {

		/**
		 * Respond to a value change by simply passing the change on to any Meems
		 * depending upon the out-bound Binary Facet.
		 *
		 * @param newValue Changed boolean value
		 */

		public synchronized void valueChanged(boolean newValue) {

			if (DebugFlag.TRACE) {
				LogTools.trace(logger, 20, "valueChanged() - invoked on BinaryStateConduit");
			}
			BinaryWedge.this.value = newValue;
			binaryClient.valueChanged(value);
		}
	}

	/* ---------- LifeCycle methds -------------------------------------------- */
	
	/**
	 * Called when the meem LifeCycle state goes from 'loaded' to 'pending'
	 */
	protected void commence() {
	}

	/**
	 * Called when the meem LifeCycle state goes from 'pending' to 'loaded'
	 */
	protected void conclude() {
		this.value = null;
	}
}
