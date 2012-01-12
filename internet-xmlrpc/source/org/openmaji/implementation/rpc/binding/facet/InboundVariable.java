/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import org.openmaji.common.NumberValue;
import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.rpc.binding.InboundBinding;


/**
 * @author Warren Bloomer
 *
 */
public class InboundVariable extends InboundBinding {

	public InboundVariable() {
		setFacetClass(Variable.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addVariableFacet(Variable listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeVariableFacet(Variable listener) {
		removeListener(listener);
	}
	
	protected void invoke(String method, Object[] params) {
		if ("valueChanged".equals(method) ) {
			Value value = null;
			Object arg = params[0];
			
			if (arg instanceof Number) {
				value = new NumberValue((Number)arg);
			}
			else {
				value = new StringValue((String)arg);
			}
			
			((Variable)proxy).valueChanged( value );
		}
	}

}
