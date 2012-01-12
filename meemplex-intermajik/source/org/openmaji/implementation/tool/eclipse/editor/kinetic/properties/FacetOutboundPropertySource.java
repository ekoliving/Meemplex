/*
 * @(#)FacetOutboundPropertySource.java
 * Created on 17/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import java.util.ArrayList;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.meem.definition.FacetOutboundAttribute;


/**
 * <code>FacetOutboundPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetOutboundPropertySource extends FacetPropertySource {
	static private final String ID_WEDGE_PUBLIC_FIELD_NAME = "wedge public field name";
	
	static PropertyDescriptor[] descriptors;
	
	static {
		ArrayList descriptorList = new ArrayList();
		FacetPropertySource.populatePropertyDescriptors(descriptorList);
		PropertyDescriptor descriptor = 
			new PropertyDescriptor(ID_WEDGE_PUBLIC_FIELD_NAME, 
					Messages.Property_FacetOutbound_WedgePublicFieldName_Label);
					
		descriptor.setFilterFlags(new String[]{PropertyFilters.FILTER_DEFINITION});
		descriptor.setDescription(Messages.Property_FacetOutbound_WedgePublicFieldName_Description);
		descriptorList.add(descriptor);
		descriptors = (PropertyDescriptor[])descriptorList.toArray(new PropertyDescriptor[0]);
	}
	
	/**
	 * Constructs an instance of <code>FacetOutboundPropertySource</code>.
	 * <p>
	 * @param facet The outbound facet to be associates with this property source.
	 */
	public FacetOutboundPropertySource(Facet facet) {
		super(facet);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.FacetPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if(id.equals(ID_WEDGE_PUBLIC_FIELD_NAME)) {
			FacetOutboundAttribute attribute = (FacetOutboundAttribute)facet.getAttribute();
			if(attribute == null) return null;
			return attribute.getWedgePublicFieldName();
		}
		else
		return super.getPropertyValue(id);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.FacetPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if(id.equals(ID_WEDGE_PUBLIC_FIELD_NAME)) {
			FacetOutboundAttribute attribute = (FacetOutboundAttribute)facet.getAttribute();
			if(attribute == null) return;
			attribute = (FacetOutboundAttribute)attribute.clone();
			attribute.setWedgePublicFieldName((String)value);
			facet.getMeem().getProxy().getMetaMeem().updateFacetAttribute(attribute);
		}
		else
		super.setPropertyValue(id, value);
	}
	
}
