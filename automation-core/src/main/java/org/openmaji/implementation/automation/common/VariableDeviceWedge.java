package org.openmaji.implementation.automation.common;

import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.meem.Wedge;

/**
 * 
 */
public class VariableDeviceWedge implements Variable, Wedge {

	/* ------ outbound facets ------------------------------------------------ */
	/**
	 * facet on which to send Linear control. 
	 */
	public Variable variableControlOutput;


	/* ------- conduits ------------------------------------------------------- */

	/**
	 * Conduit on which to receive Linear control
	 */
	public Variable variableControlConduit = new VariableControlConduit();

	/**
	 * Conduit on which to send Linear state.
	 */
	public Variable variableStateConduit;


	/* ---------- Linear interface ------------------------------------------- */

	public void valueChanged(Value value) {
		variableStateConduit.valueChanged(value);
	}


	/* ---------- LinearControlConduit ----------------------------------------- */

	private final class VariableControlConduit implements Variable {
		public void valueChanged(Value value) {
			variableControlOutput.valueChanged(value);
		}
	}
}
