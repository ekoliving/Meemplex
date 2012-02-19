/*
 * @(#)PropertyCellFactory.java
 * Created on 16/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;


/**
 * <code>PropertyCellFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertyCellFactory {
	static CellEditor create(Composite parent, Class type) {
		if(type.equals(Integer.class)) {
				return new IntegerCellEditor(parent);
		}
		return new TextCellEditor(parent);
	}
}
