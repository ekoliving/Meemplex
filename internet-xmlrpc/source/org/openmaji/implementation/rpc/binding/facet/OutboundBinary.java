/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;

import org.openmaji.common.Binary;
import org.openmaji.rpc.binding.OutboundBinding;


/**
 * An outbound Boolean Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundBinary extends OutboundBinding implements Binary {

	/**
	 *
	 */
	public OutboundBinary() {
		setFacetClass(Binary.class);
	}
	
	/**
	 * Send a value to the remove Meem
	 */
	public void valueChanged(boolean b) {		
		send("valueChanged", new Serializable[] { new Boolean(b) });
	}

}
