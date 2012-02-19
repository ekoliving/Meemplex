/*
 * @(#)WedgeLoadedPropertySource.java
 * Created on 15/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import java.util.ArrayList;
import java.util.Collection;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.implementation.tool.eclipse.ui.property.FieldsPropertyDescriptor;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>WedgeLoadedPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeLoadedPropertySource implements IPropertySource {
	static private final String ID_PERSISTENT_FIELDS = "persistent fields";

	static private final String ID_IMPLEMENTATION_CLASS_NAME = "implementation class name";

	static private final String ID_IDENTIFIER = "identifier";

	private Wedge wedge;

	private ArrayList<PropertyDescriptor> descriptors;

	/**
	 * Constructs an instance of <code>WedgeLoadedPropertySource</code>.
	 * <p>
	 * @param wedge the wedge associates with this wedge property source.
	 */
	public WedgeLoadedPropertySource(Wedge wedge) {
		this.wedge = wedge;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		WedgeAttribute wedgeAttribute = wedge.getAttribute();
		if (wedgeAttribute == null)
			return new IPropertyDescriptor[0];
		wedgeAttribute = (WedgeAttribute) wedgeAttribute.clone();

		if (descriptors == null) {
			descriptors = new ArrayList<PropertyDescriptor>();
			descriptors.add(new FieldsPropertyDescriptor(ID_PERSISTENT_FIELDS, Messages.Property_Wedge_PersistentFields_Label, wedgeAttribute.getImplementationClassName(), "Pick the field(s) to be persistent."));
			//			descriptors.add(new ClassPropertyDescriptor(ID_IMPLEMENTATION_CLASS_NAME, Messages.Property_Wedge_ImplementationClassName_Label));
			descriptors.add(new PropertyDescriptor(ID_IMPLEMENTATION_CLASS_NAME, Messages.Property_Wedge_ImplementationClassName_Label));
			descriptors.add(new PropertyDescriptor(ID_IDENTIFIER, Messages.Property_Wedge_Identifier_Label));

			((PropertyDescriptor) descriptors.get(0)).setFilterFlags(new String[] { PropertyFilters.FILTER_DEFINITION });
			((PropertyDescriptor) descriptors.get(0)).setDescription(Messages.Property_Wedge_PersistentFields_Description);

			((PropertyDescriptor) descriptors.get(1)).setFilterFlags(new String[] { PropertyFilters.FILTER_DEFINITION });
			((PropertyDescriptor) descriptors.get(1)).setDescription(Messages.Property_Wedge_ImplementationClassName_Description);

			((PropertyDescriptor) descriptors.get(2)).setFilterFlags(new String[] { PropertyFilters.FILTER_DEFINITION });
			((PropertyDescriptor) descriptors.get(2)).setDescription(Messages.Property_Wedge_Identifier_Description);
		}

		return (IPropertyDescriptor[]) descriptors.toArray(new IPropertyDescriptor[0]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		WedgeAttribute wedgeAttribute = wedge.getAttribute();
		if (wedgeAttribute == null) {
			return null;
		}

		if (id.equals(ID_PERSISTENT_FIELDS)) {
			return wedgeAttribute.getPersistentFields();
		}
		else if (id.equals(ID_IMPLEMENTATION_CLASS_NAME)) {
			return wedgeAttribute.getImplementationClassName();
		}
		else if (id.equals(ID_IDENTIFIER)) {
			return wedgeAttribute.getIdentifier();
		}
		else {
			return null;
		}
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
		WedgeAttribute wedgeAttribute = wedge.getAttribute();
		if (wedgeAttribute == null)
			return;
		MetaMeem metaMeem = wedge.getMeem().getProxy().getMetaMeem();
		if (metaMeem == null)
			return;

		wedgeAttribute = (WedgeAttribute) wedgeAttribute.clone();

		if (id.equals(ID_PERSISTENT_FIELDS)) {
			wedgeAttribute.setPersistentFields((Collection<String>) value);
			metaMeem.updateWedgeAttribute(wedgeAttribute);
		}
		else if (id.equals(ID_IMPLEMENTATION_CLASS_NAME)) {
			System.err.println("WedgeLoadedProperty.setPropertyValue() - should we enable this?");
			//wedgeAttribute.setImplementationClassName((String)value);
			//metaMeem.updateWedgeAttribute(wedgeAttribute);
		}
		else if (id.equals(ID_IDENTIFIER)) {
			wedgeAttribute.setIdentifier((String) value);
			metaMeem.updateWedgeAttribute(wedgeAttribute);
		}
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "";
	}
}
