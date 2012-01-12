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

import org.openmaji.system.space.CategoryClient;

/**
 * @author Warren Bloomer
 *
 */
public class InboundCategoryClientBeanInfo extends SimpleBeanInfo {

	EventSetDescriptor[] eventSetDescriptors = null;
	
	/**
	 * 
	 */
	public EventSetDescriptor[] getEventSetDescriptors() {
		
		if (eventSetDescriptors == null) {
			try {
				eventSetDescriptors = new EventSetDescriptor[] {
						new EventSetDescriptor(
								InboundCategoryClient.class,	       // source class
								"receiveCategoryClient", 		       // event set name
								CategoryClient.class, 	               // listener type
								new String[]{ 
										"entriesAdded", 
										"entriesRemoved", 
										"entryRenamed"
									},                                 // listener method names
								"addCategoryClientFacet",		       // add listener method
								"removeCategoryClientFacet"		       // remove listener method
							),
				};
			}
			catch (IntrospectionException ex) {
				System.err.println("Could not create EventSetDescriptor for InboundCategoryClientBeanInfo.");
			}
		}
		return eventSetDescriptors;
	}
}
