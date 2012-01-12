/*
 * Created on 20/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;

import org.openmaji.common.LinearList;

/**
 * @author Warren Bloomer
 *
 */
public class InboundLinearListBeanInfo extends SimpleBeanInfo {
	
	EventSetDescriptor[] eventSetDescriptors = null;
	
	/**
	 * 
	 */
	public EventSetDescriptor[] getEventSetDescriptors() {
		
		if (eventSetDescriptors == null) {
			try {
				eventSetDescriptors = new EventSetDescriptor[] {
						new EventSetDescriptor(
								InboundLinearList.class,	   // source class
								"receiveLinearList", 		   // event set name
								LinearList.class, 	       // listener type
								new String[]{"valueChanged"},  // listener method names
								"addLinearListFacet",		   // add listener method
								"removeLinearListFacet"      // remove listener method
							),
				};
			}
			catch (IntrospectionException ex) {
				System.err.println("Could not create EventSetDescriptor for InboundLinearListBeanInfo.");
			}
		}
		return eventSetDescriptors;
	}
}
