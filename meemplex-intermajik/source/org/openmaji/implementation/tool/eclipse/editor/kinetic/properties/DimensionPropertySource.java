/*
 * @(#)DimensionPropertySource.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import org.eclipse.draw2d.geometry.Dimension;
import org.openmaji.implementation.tool.eclipse.editor.common.Messages;

public class DimensionPropertySource implements IPropertySource {
	public static String ID_WIDTH = "width";
	public static String ID_HEIGHT = "height";
	protected static IPropertyDescriptor[] descriptors;

	static {
		descriptors =
			new IPropertyDescriptor[] {
				new TextPropertyDescriptor(ID_WIDTH, Messages.DimensionPropertySource_Property_Width_Label),
				new TextPropertyDescriptor(ID_HEIGHT, Messages.DimensionPropertySource_Property_Height_Label)};
	}

	protected Dimension dimension = null;

	public DimensionPropertySource(Dimension dimension) {
		this.dimension = new Dimension(dimension);
	}

	public Object getEditableValue() {
		return this;
	}

	public Object getPropertyValue(Object propName) {
		return getPropertyValue((String) propName);
	}

	public Object getPropertyValue(String propName) {
		if (ID_HEIGHT.equals(propName)) {
			return new String(new Integer(dimension.height).toString());
		}
		if (ID_WIDTH.equals(propName)) {
			return new String(new Integer(dimension.width).toString());
		}
		return null;
	}

	public Dimension getValue() {
		return new Dimension(dimension);
	}

	public void setPropertyValue(Object propName, Object value) {
		setPropertyValue((String) propName, value);
	}

	public void setPropertyValue(String propName, Object value) {
		if (ID_HEIGHT.equals(propName)) {
			Integer newInt = new Integer((String) value);
			dimension.height = newInt.intValue();
		}
		if (ID_WIDTH.equals(propName)) {
			Integer newInt = new Integer((String) value);
			dimension.width = newInt.intValue();
		}
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public void resetPropertyValue(String propName) {
	}

	public void resetPropertyValue(Object propName) {
	}

	public boolean isPropertySet(Object propName) {
		return true;
	}

	public boolean isPropertySet(String propName) {
		if (ID_HEIGHT.equals(propName) || ID_WIDTH.equals(propName))
			return true;
		return false;
	}

	public String toString() {
		return new String("(" + dimension.width + "," + dimension.height + ")");
	}

}