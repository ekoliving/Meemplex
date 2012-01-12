/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.ArrayList;

import org.openmaji.common.NumberValue;
import org.openmaji.common.Value;
import org.openmaji.common.VariableList;
import org.openmaji.rpc.binding.OutboundBinding;


/**
 * An outbound VariableList Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundVariableList extends OutboundBinding implements VariableList {
	
	/**
	 *
	 */
	public OutboundVariableList() {
		setFacetClass(VariableList.class);
	}
	
	/**
	 * Send a value to the remove Meem
	 */
	public void valueChanged(Value[] valueList) {

		ArrayList<Serializable> valueVector = new ArrayList<Serializable>();
		if (valueList != null) {
			for (int i=0; i<valueList.length; i++) {
				Value v = valueList[i];
				if (v instanceof NumberValue) {
					valueVector.add( new Float(v.floatValue()) );
				}
				else { //if (p instanceof FloatPosition) {
					valueVector.add( new String(v.toString()) );
				}
				
			}
			
		}
		
		send("valueChanged", new Serializable[] { valueVector });
	}
	
}
