/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.ArrayList;

import org.openmaji.common.FloatPosition;
import org.openmaji.common.IntegerPosition;
import org.openmaji.common.LinearList;
import org.openmaji.common.Position;
import org.openmaji.rpc.binding.OutboundBinding;


/**
 * An outbound VariableList Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundLinearList extends OutboundBinding implements LinearList {
	
	/**
	 *
	 */
	public OutboundLinearList() {
		setFacetClass(LinearList.class);
	}
	
	/**
	 * Send a value to the remove Meem
	 */
	public void valueChanged(Position[] valueList) {

		ArrayList<ArrayList<Number>> valueVector = new ArrayList<ArrayList<Number>>();
		if (valueList != null) {
			for (int i=0; i<valueList.length; i++) {
				Position v = valueList[i];
				if (v instanceof IntegerPosition) {
					IntegerPosition ip = (IntegerPosition) v;
					ArrayList<Number> vect = new ArrayList<Number>();
					vect.add( new Integer(ip.intValue()) );
					vect.add( new Integer(ip.getMinimumAsInt()) );
					vect.add( new Integer(ip.getMaximumAsInt()) );
					valueVector.add(vect);
				}
				else if (v instanceof FloatPosition ){
					FloatPosition fp = (FloatPosition) v;
					ArrayList<Number> vect = new ArrayList<Number>();
					vect.add( new Double(fp.doubleValue()) );
					vect.add( new Double(fp.getMinimumAsFloat()) );
					vect.add( new Double(fp.getMaximumAsFloat()) );
					valueVector.add( vect );
				}
				else {
					ArrayList<Number> vect = new ArrayList<Number>();
					vect.add( new Double(v.doubleValue()) );
					vect.add( new Double(Double.MIN_VALUE) );
					vect.add( new Double(Double.MAX_VALUE) );
					valueVector.add( vect );					
				}
				
			}			
		}
		
		send("Serializable", new Serializable[] { valueVector });
	}
	
}
