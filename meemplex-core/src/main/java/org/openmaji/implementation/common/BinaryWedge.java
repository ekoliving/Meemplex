/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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

import org.meemplex.meem.Conduit;
import org.meemplex.meem.Facet;
import org.meemplex.meem.FacetContent;
import org.meemplex.service.model.Direction;
import org.openmaji.common.Binary;
import org.openmaji.implementation.common.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;




import java.util.logging.Level;
import java.util.logging.Logger;

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

@org.meemplex.meem.Wedge
public class BinaryWedge implements Binary, Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();

	@Facet(direction=Direction.IN, name="binaryInput")
	public Binary binaryInput = this;
	
	/**
	 * Binary client (out-bound Facet)
	 */
	@Facet(direction=Direction.OUT, name="binaryOutput")
	public Binary binaryClient;

	/**
	 * Send content to a Binary client that has just had its Reference added.
	 */
	@FacetContent(facet="binaryOutput")
	public final ContentProvider binaryClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) {
			if (DebugFlag.TRACE) {
				logger.log(Level.FINE, "sendContent() - invoked");
			}
			
			// only send value if one has been received on the binaryStateConduit, otherwise the value is invalid.
			if (value != null) {
				((Binary) target).valueChanged(value);
			}
		}
	};

	/**
	 * The conduit through which other Wedges in the Meem are notified of a
	 * request to change the value of the binary thing.  
	 */

	@Conduit(name="binaryControl")
	public Binary binaryControlConduit;

	/**
	 * The conduit through which state changes are received other Wedges in the Meem. 
	 */

	@Conduit(name="binaryState")
	public Binary binaryStateConduit = new BinaryStateConduit();
	
	/**
	 * LifeCycleClient conduit.
	 */
	
	@Conduit(name="lifeCycleClient")
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
			logger.log(Level.FINE, "valueChanged() - invoked on in-bound facet");
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
				logger.log(Level.FINE, "valueChanged() - invoked on BinaryStateConduit");
			}
			BinaryWedge.this.value = newValue;
			binaryClient.valueChanged(value);
		}
	}

	/* ---------- LifeCycle methds -------------------------------------------- */
	
	/**
	 * Called when the meem LifeCycle state goes from 'loaded' to 'pending'
	 */
	//@OnCommence
	protected void commence() {
	}

	/**
	 * Called when the meem LifeCycle state goes from 'pending' to 'loaded'
	 */
	//@OnConlude
	protected void conclude() {
		this.value = null;
	}
}
