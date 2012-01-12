/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;

import org.openmaji.common.NumberValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.rpc.binding.OutboundBinding;


/**
 * An outbound Boolean Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundVariable extends OutboundBinding implements Variable {

	/**
	 *
	 */
	public OutboundVariable() {
		setFacetClass(Variable.class);
	}
	
	/**
	 * Send a value to the remove Meem
	 */
	public void valueChanged(Value v) {

		Serializable value = null;
		if (v instanceof NumberValue) {
			value = new Float(v.floatValue());
		}
		else { //if (p instanceof FloatPosition) {
			value = new String(v.toString());
		}
		send("valueChanged", new Serializable[] { value });
	}
	
}
