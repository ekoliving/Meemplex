/*
 * @(#)FacetPropertySource.java
 * Created on 15/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import java.util.List;


import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.meem.definition.FacetAttribute;


/**
 * <code>FacetPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class FacetPropertySource implements IPropertySource {
	static protected final String ID_IDENTIFIER = "identifier";
	static protected final String ID_INTERFACE_NAME = "interface name";

	protected Facet facet;
	
	protected FacetPropertySource(Facet facet) {
		this.facet = facet;
	}
	
	static protected void populatePropertyDescriptors(List descriptors)	{
		PropertyDescriptor 
		descriptor = new TextPropertyDescriptor(ID_IDENTIFIER, Messages.Property_Facet_Identifier_Label);
		descriptor.setFilterFlags(new String[]{PropertyFilters.FILTER_DEFINITION});
		descriptor.setDescription(Messages.Property_Facet_Identifier_Description);
		descriptors.add(descriptor);
		
		descriptor = new PropertyDescriptor(ID_INTERFACE_NAME, Messages.Property_Facet_InterfaceName_Label);
		descriptor.setFilterFlags(new String[]{PropertyFilters.FILTER_DEFINITION});
		descriptor.setDescription(Messages.Property_Facet_InterfaceName_Description);
		descriptors.add(descriptor);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if(id.equals(ID_IDENTIFIER)) {
			return facet.getAttribute().getIdentifier();
		}
		else
		if(id.equals(ID_INTERFACE_NAME)) {
			return facet.getAttribute().getInterfaceName();
		}
		else
		return null;
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
		if(facet.getAttribute() == null) return;
		
		FacetAttribute attribute = (FacetAttribute)facet.getAttribute().clone();

		if(id.equals(ID_IDENTIFIER)) {
			attribute.setIdentifier((String)value);
			facet.getMeem().getProxy().getMetaMeem().updateFacetAttribute(attribute);
		}
		/*
		else
		if(id.equals(ID_INTERFACE_NAME)) {
			attribute.setInterfaceName((String)value);
			facet.getMeem().getProxy().getMetaMeem().updateFacetAttribute(attribute);
		}
		*/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
			return false;
	}
}
