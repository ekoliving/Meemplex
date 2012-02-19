/*
 * @(#)PropertySource.java
 * Created on 10/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import org.eclipse.ui.views.properties.IPropertySource;

/**
 * <code>PropertySource</code>.<p>
 * @author Kin Wong
 */
abstract public class PropertySource implements IPropertySource {
	boolean readOnly;
	
	protected PropertySource(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	protected boolean isReadOnly() {
		return readOnly;
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
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if(isReadOnly()) return;
		setModifiableValue(id, value);
	}
	
	abstract protected void setModifiableValue(Object id, Object value);
}
