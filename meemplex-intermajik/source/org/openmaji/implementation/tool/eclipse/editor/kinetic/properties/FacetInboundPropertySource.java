/*
 * @(#)FacetInboundPropertySource.java
 * Created on 17/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import java.util.ArrayList;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.properties.BooleanComboBoxPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.meem.definition.FacetInboundAttribute;


/**
 * <code>FacetInboundPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetInboundPropertySource extends FacetPropertySource {
	static private final String ID_CONTENT_REQUIRED = "content required";
	static private PropertyDescriptor[] descriptors;
	
	static {
		ArrayList descriptorList = new ArrayList();
		FacetPropertySource.populatePropertyDescriptors(descriptorList);
		PropertyDescriptor descriptor = 
			new BooleanComboBoxPropertyDescriptor(ID_CONTENT_REQUIRED, 
					Messages.Property_FacetInbound_ContentRequired_Label);
					
		descriptor.setFilterFlags(new String[]{PropertyFilters.FILTER_DEFINITION});
		descriptor.setDescription(Messages.Property_FacetInbound_ContentRequired_Description);
		descriptorList.add(descriptor);
		
		descriptors = (PropertyDescriptor[])descriptorList.toArray(new PropertyDescriptor[0]);
	}
	
	public FacetInboundPropertySource(Facet facet) {
		super(facet);
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.FacetPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if(id.equals(ID_CONTENT_REQUIRED)) {
			FacetInboundAttribute attribute = (FacetInboundAttribute)facet.getAttribute();
			if(attribute == null) return null;
			return new Boolean(attribute.isContentRequired());
		}
		else
		return super.getPropertyValue(id);
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.FacetPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if(id.equals(ID_CONTENT_REQUIRED)) {
			FacetInboundAttribute attribute = (FacetInboundAttribute)facet.getAttribute();
			if(attribute == null) return;
			attribute = (FacetInboundAttribute)attribute.clone();
			attribute.setContentRequired(((Boolean)value).booleanValue());
			facet.getMeem().getProxy().getMetaMeem().updateFacetAttribute(attribute);
		}
		else
		super.setPropertyValue(id, value);
	}
}
