/*
 * @(#)MeemAttributePropertySource.java
 * Created on 16/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.properties.ObjectComboBoxPropertyDescriptor;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.Scope;


/**
 * <code>MeemAttributePropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemAttributePropertySource implements IPropertySource {
	static private final String ID_IDENTIFIER = "identifier";
	static private final String ID_SCOPE = "scope";
	static private final String ID_VERSION = "version";
	
	static private PropertyDescriptor[] descriptors;
	
	static {
		Object[] scopes = new Object[] {
				Scope.DISTRIBUTED,
				Scope.FEDERATED,
				Scope.LOCAL,
				Scope.MEEMPLEX
		};
		
		descriptors = new PropertyDescriptor[] {
			new PropertyDescriptor(ID_IDENTIFIER, "Identifier"),
			new ObjectComboBoxPropertyDescriptor(ID_SCOPE, "Scope", scopes),
			new PropertyDescriptor(ID_VERSION, "Version")
		};
	}

	private MeemAttribute meemAttribute;
	
	/**
	 * Constructs an instance of <code>MeemAttributePropertySource</code>.
	 * <p>
	 * @param meemAttribute The initial meem attribute.
	 */
	public MeemAttributePropertySource(MeemAttribute meemAttribute) {
		this.meemAttribute = (MeemAttribute)meemAttribute.clone();
	}
	
	public MeemAttribute getMeemAttribute() {
		return meemAttribute;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return this; 		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if(id.equals(ID_IDENTIFIER)) {
			return meemAttribute.getIdentifier();
		}
		else
		if(id.equals(ID_SCOPE)) {
			return meemAttribute.getScope();
		}
		else
		if(id.equals(ID_VERSION)) {
			return new Integer(meemAttribute.getVersion());
		}
		else
		return null;
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
		if(id.equals(ID_IDENTIFIER)) {
			meemAttribute.setIdentifier((String)value);
		}
		else
		if(id.equals(ID_SCOPE)) {
			meemAttribute.setScope((Scope)value);
		}
		else
		if(id.equals(ID_VERSION)) {
			meemAttribute.setVersion(((Integer)value).intValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return 	meemAttribute.getIdentifier() + ", " + 
						meemAttribute.getScope() + ", Version " +
						meemAttribute.getVersion();
	}
}
