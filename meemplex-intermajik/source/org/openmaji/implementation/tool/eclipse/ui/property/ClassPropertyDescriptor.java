/*
 * @(#)ClassPropertyDescriptor.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.property;

import java.util.Collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author mg
 * Created on 14/01/2003
 */
public class ClassPropertyDescriptor extends PropertyDescriptor {
	
	private Collection excludeList = null;
	private Collection includeList = null;
	
	public ClassPropertyDescriptor(String name, String displayName) {
		super(name, displayName);
	}

	public ClassPropertyDescriptor(String name, String displayName, Collection includeList, Collection excludeList) {
		super(name, displayName);
		this.includeList = includeList;
		this.excludeList = excludeList;
	}

	
	public CellEditor createPropertyEditor(Composite parent) {
		return new ClassPropertyCellEditor(parent, includeList, excludeList);
	}
}
