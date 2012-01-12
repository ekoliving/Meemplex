/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;

import org.openmaji.common.FloatPosition;
import org.openmaji.common.IntegerPosition;
import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.rpc.binding.OutboundBinding;


/**
 * An outbound Boolean Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundLinear extends OutboundBinding implements Linear {

	/**
	 *
	 */
	public OutboundLinear() {
		setFacetClass(Linear.class);
	}
	
	/**
	 * Send a value to the remove Meem
	 */
	public void valueChanged(Position p) {

		Number value = null;
		Number minimum;
		Number maximum;
		if (p instanceof IntegerPosition) {
			IntegerPosition ip = (IntegerPosition) p;
			value   = new Integer(ip.intValue());
			minimum = new Integer(ip.getMinimumAsInt());
			maximum = new Integer(ip.getMaximumAsInt());
		}
		else if (p instanceof FloatPosition) {
			FloatPosition fp = (FloatPosition) p;
			value   = new Double(fp.doubleValue());
			minimum = new Double(fp.getMinimumAsFloat());
			maximum = new Double(fp.getMaximumAsFloat());
		}
		else {
			value   = new Double(p.doubleValue());
			minimum = new Double(p.getMinimumAsDouble());
			maximum = new Double(p.getMaximumAsDouble());	
		}
		send("valueChanged", new Serializable[] { value, minimum, maximum });
	}
	
}
