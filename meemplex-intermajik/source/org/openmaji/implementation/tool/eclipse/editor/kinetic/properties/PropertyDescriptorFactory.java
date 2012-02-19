/*
 * @(#)PropertyDescriptorFactory.java
 * Created on 28/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;


import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.ui.property.ConfigurationCellEditorValidator;
import org.openmaji.implementation.tool.eclipse.ui.property.IntegerPropertyDescriptor;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;


/**
 * <code>PropertyDescriptorFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertyDescriptorFactory {
	static private PropertyDescriptorFactory _instance 
	= new PropertyDescriptorFactory();
	
	static public PropertyDescriptorFactory getInstance() {
		return _instance;
	}
	
	/**
	 * Constructs an instance of <code>PropertyDescriptorFactory</code>.
	 * <p>
	 * 
	 */
	private PropertyDescriptorFactory() {
	}
	
	public PropertyDescriptor create(ConfigurationSpecification specification) {
		Class type = specification.getType();
		Object id = specification.getIdentifier();
		
		// add wedge Id to make it clear which propert is being referred to
		//String displayName = specification.getIdentifier().getWedgeIdentifier() + "." + specification.getIdentifier().getName();
		String displayName = specification.getIdentifier().getAlias();
		
		PropertyDescriptor descriptor = null;
		if(type.equals(Integer.class)) {
			descriptor = new IntegerPropertyDescriptor(id, displayName);
		}
		else
		descriptor = new TextPropertyDescriptor(id, displayName);
		descriptor.setValidator(new ConfigurationCellEditorValidator(specification));
		return descriptor;
	}
}
