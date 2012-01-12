package org.openmaji.implementation.automation.common;

import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.meem.Wedge;

/**
 * 
 */

public class LinearDeviceWedge implements Linear, Wedge {
	
	
	/* ------ outbound facets ------------------------------------------------ */
	/**
	 * facet on which to send Linear control. 
	 */
	public Linear linearControlOutput;
	
	
	/* ------- conduits ------------------------------------------------------- */
	
	/**
	 * Conduit on which to receive Linear control
	 */
	public Linear linearControlConduit = new LinearControlConduit();

	/**
	 * Conduit on which to send Linear state.
	 */
	public Linear linearStateConduit;

	
	/* ---------- Linear interface ------------------------------------------- */
	
	public void valueChanged(Position value) {
		linearStateConduit.valueChanged(value);
	}


	/* ---------- LinearControlConduit ----------------------------------------- */

	private final class LinearControlConduit implements Linear {
		/**
		 * Send value to linearControl outbound facet.
		 *
		 * @param value Changed boolean value
		 */
		public synchronized void valueChanged(Position value) {
			linearControlOutput.valueChanged(value);
		}
	}
}
