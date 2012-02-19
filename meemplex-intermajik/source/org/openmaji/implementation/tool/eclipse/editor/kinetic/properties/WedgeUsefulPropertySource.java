/*
 * @(#)WedgeUsefulPropertySource.java
 * Created on 27/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import java.util.ArrayList;
import java.util.Iterator;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.meem.definition.WedgeAttribute;


/**
 * <code>WedgeUsefulPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeUsefulPropertySource implements IPropertySource {
	private ArrayList<TextPropertyDescriptor> descriptors;
	private Wedge wedge;
	/**
	 * Constructs an instance of <code>WedgeLoadedPropertySource</code>.
	 * <p>
	 */
	public WedgeUsefulPropertySource(Wedge wedge) {
		this.wedge = wedge;
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
		if(descriptors != null) 
			return (IPropertyDescriptor[])descriptors.toArray(new IPropertyDescriptor[0]);

		WedgeAttribute wedgeAttribute = wedge.getAttribute();
		if(wedgeAttribute == null) return null;
		
		descriptors = new ArrayList<TextPropertyDescriptor>();
		Iterator<String> it = wedgeAttribute.getPersistentFields().iterator();
		while(it.hasNext()) {
			String field = it.next();
			descriptors.add(new TextPropertyDescriptor(field, field));
		}
		return (IPropertyDescriptor[])descriptors.toArray(new IPropertyDescriptor[0]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
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
	}
}
