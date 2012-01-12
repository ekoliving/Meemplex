/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.util.Vector;

import org.openmaji.common.FloatPosition;
import org.openmaji.common.IntegerPosition;
import org.openmaji.common.LinearList;
import org.openmaji.common.Position;
import org.openmaji.rpc.binding.InboundBinding;


/**
 * @author Warren Bloomer
 *
 */
public class InboundLinearList extends InboundBinding {

	public InboundLinearList() {
		setFacetClass(LinearList.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addLinearListFacet(LinearList listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeLinearListFacet(LinearList listener) {
		removeListener(listener);
	}
	
	protected void invoke(String method, Object[] params) {
		if ("valueChanged".equals(method) ) {
			Vector list = (Vector) params[0];
			
			Position[] valueList = new Position[list.size()];
			
			for (int i=0; i<valueList.length; i++) {
				Position value = null;
				Vector v = (Vector)list.get(i);
				Object num = v.get(0);
				if (num instanceof Integer) {
					value = new IntegerPosition(
							((Integer)num).intValue(),
							1,
							((Integer)v.get(1)).intValue(),
							((Integer)v.get(2)).intValue()
						);
				}
				else if (num instanceof Number){
					value = new FloatPosition(
							((Double)num).floatValue(),
							1,
							((Double)v.get(1)).floatValue(),
							((Double)v.get(2)).floatValue()
						);
				}
				else {
					// huh?
					value = new FloatPosition(0f);
				}
				valueList[i] = value;
			}			

			((LinearList)proxy).valueChanged( valueList );
		}
	}

}
