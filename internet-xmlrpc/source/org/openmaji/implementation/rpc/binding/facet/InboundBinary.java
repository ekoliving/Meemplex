/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import org.openmaji.common.Binary;
import org.openmaji.rpc.binding.InboundBinding;


/**
 * @author Warren Bloomer
 *
 */
public class InboundBinary extends InboundBinding {

	public InboundBinary() {
		setFacetClass(Binary.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addBinaryFacet(Binary listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeBinaryFacet(Binary listener) {
		removeListener(listener);
	}
	
	protected void invoke(String method, Object[] params) {
		if ("valueChanged".equals(method) ) {
			((Binary)proxy).valueChanged( ((Boolean)params[0]).booleanValue() );
		}
	}

}
