package org.openmaji.implementation.automation.common;

import org.openmaji.common.Binary;
import org.openmaji.meem.Wedge;

/**
 * 
 */

public class BinaryDeviceWedge implements Binary, Wedge {
	
	
	/* ------ outbound facets ------------------------------------------------ */
	/**
	 * facet on which to send binary control. 
	 */
	public Binary binaryControlOutput;
	
	
	/* ------- conduits ------------------------------------------------------- */
	
	/**
	 * Conduit on which to receive binary control
	 */
	public Binary binaryControlConduit = new BinaryControlConduit();

	/**
	 * Conduit on which to send binary state.
	 */
	public Binary binaryStateConduit;

	
	/* ---------- Binary interface ------------------------------------------- */
	
	public void valueChanged(boolean value) {
		binaryStateConduit.valueChanged(value);
	}


	/* ---------- BinaryControlConduit ----------------------------------------- */

	private final class BinaryControlConduit implements Binary {
		/**
		 * Send value to binaryControl outbound facet.
		 *
		 * @param value Changed boolean value
		 */
		public synchronized void valueChanged(boolean value) {
			binaryControlOutput.valueChanged(value);
		}
	}
}
