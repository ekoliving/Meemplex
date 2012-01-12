/*
 * Created on 20/08/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;

import org.openmaji.common.Variable;

/**
 * @author Warren Bloomer
 *
 */
public class InboundVariableBeanInfo extends SimpleBeanInfo {
	
	EventSetDescriptor[] eventSetDescriptors = null;
	
	/**
	 * 
	 */
	public EventSetDescriptor[] getEventSetDescriptors() {
		
		if (eventSetDescriptors == null) {
			try {
				eventSetDescriptors = new EventSetDescriptor[] {
						new EventSetDescriptor(
								InboundVariable.class,	       // source class
								"receiveVariable", 		       // event set name
								Variable.class, 	               // listener type
								new String[]{"valueChanged"},  // listener method names
								"addVariableFacet",		       // add listener method
								"removeVariableFacet"		       // remove listener method
							),
				};
			}
			catch (IntrospectionException ex) {
				System.err.println("Could not create EventSetDescriptor for InboundVariableBeanInfo.");
			}
		}
		return eventSetDescriptors;
	}
}
