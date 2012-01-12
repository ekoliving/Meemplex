/*
 * @(#)ConfigurationPropertySource.java
 * Created on 9/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.property;

//import org.openmaji.meem.wedge.configuration.ConfigurationClient;

import java.io.Serializable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.PropertyDescriptorFactory;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.PropertyFilters;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;


/**
 * <code>ConfigurationPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class ConfigurationPropertySource implements IPropertySource {
	private ConfigurationHandlerProxy configuration;
	private IPropertyDescriptor[] descriptors;
	
	/**
	 * Constructs an instance of <code>ConfigurationPropertySource</code>.
	 * <p>
	 * @param configurationHandler
	 */
	public ConfigurationPropertySource(ConfigurationHandlerProxy configurationHandler) {
		this.configuration = configurationHandler;
		//configurationHandler.addClient(configurationClient);
		refreshDescriptors();
	}
	
	
	protected void refreshDescriptors() {
		ConfigurationSpecification[] specs = configuration.getSpecifications();
		descriptors = new IPropertyDescriptor[specs.length];
		for(int i = 0; i < specs.length; i++) {
			ConfigurationSpecification spec = specs[i];
			descriptors[i] = createDescriptor(spec);
		}
	}
	
	private IPropertyDescriptor createDescriptor(ConfigurationSpecification specification) {
		
		PropertyDescriptor descriptor = PropertyDescriptorFactory.getInstance().
			create(specification);
				
		descriptor.setDescription(specification.getDescription());
		descriptor.setFilterFlags(new String[]{PropertyFilters.FILTER_CONFIGURATION});
		return descriptor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object value = configuration.getValue(id);
		if(value == null) {
			ConfigurationSpecification specification = configuration.getSpecification(id);
			if(specification == null) return "";
			if (specification.getDefaultValue() != null)
				return specification.getDefaultValue();
			
			return "";
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		ConfigurationSpecification specification = configuration.getSpecification(id);
		configuration.valueChanged(specification.getIdentifier(), (Serializable) value);
		//System.out.println("Value Changing: " + specification.toString() + value);
	}
}
