/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.util.Vector;

import org.openmaji.common.NumberValue;
import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.VariableList;
import org.openmaji.rpc.binding.InboundBinding;


/**
 * @author Warren Bloomer
 *
 */
public class InboundVariableList extends InboundBinding {

	public InboundVariableList() {
		setFacetClass(VariableList.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addVariableListFacet(VariableList listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeVariableListFacet(VariableList listener) {
		removeListener(listener);
	}
	
	protected void invoke(String method, Object[] params) {
		if ("valueChanged".equals(method) ) {
			Vector list = (Vector) params[0];
			
			Value[] valueList = new Value[list.size()];
			
			for (int i=0; i<valueList.length; i++) {
				Value value = null;
				Object v = list.get(i);
				if (v instanceof Number) {
					value = new NumberValue((Number)v);
				}
				else {
					value = new StringValue((String)v);
				}
				valueList[i] = value;
			}			
			
			((VariableList)proxy).valueChanged( valueList );
		}
	}

}
