/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import org.openmaji.common.FloatPosition;
import org.openmaji.common.IntegerPosition;
import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.rpc.binding.InboundBinding;


/**
 * @author Warren Bloomer
 *
 */
public class InboundLinear extends InboundBinding {

	public InboundLinear() {
		setFacetClass(Linear.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addLinearFacet(Linear listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeLinearFacet(Linear listener) {
		removeListener(listener);
	}
	
	protected void invoke(String method, Object[] params) {
		if ("valueChanged".equals(method) ) {
			Position position = null;
			Object arg = params[0];
			Object minimum = params[1];
			Object maximum = params[2];
			
			if (arg instanceof Integer) {
				position = new IntegerPosition(
						((Integer)arg).intValue(),
						1,
						((Number)minimum).intValue(),
						((Number)maximum).intValue()
					);
			}
			else if (arg instanceof Double) {
				position = new FloatPosition(
						((Double)arg).floatValue(),
						1f,
						((Number)minimum).floatValue(),
						((Number)maximum).floatValue()
					);
			}
			else {
				position = new FloatPosition(
						((Double)arg).floatValue(),
						1f,
						((Number)minimum).floatValue(),
						((Number)maximum).floatValue()
					);
			}
			
			((Linear)proxy).valueChanged( position );
		}
	}

}
