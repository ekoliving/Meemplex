/*
 * Created on 20/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;

import org.openmaji.common.VariableList;

/**
 * @author Warren Bloomer
 *
 */
public class InboundVariableListBeanInfo extends SimpleBeanInfo {
	
	EventSetDescriptor[] eventSetDescriptors = null;
	
	/**
	 * 
	 */
	public EventSetDescriptor[] getEventSetDescriptors() {
		
		if (eventSetDescriptors == null) {
			try {
				eventSetDescriptors = new EventSetDescriptor[] {
						new EventSetDescriptor(
								InboundVariableList.class,	   // source class
								"receiveVariableList", 		   // event set name
								VariableList.class, 	       // listener type
								new String[]{"valueChanged"},  // listener method names
								"addVariableListFacet",		   // add listener method
								"removeVariableListFacet"      // remove listener method
							),
				};
			}
			catch (IntrospectionException ex) {
				System.err.println("Could not create EventSetDescriptor for InboundVariableListBeanInfo.");
			}
		}
		return eventSetDescriptors;
	}
}
