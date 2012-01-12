/*
 * @(#)IntegerCellEditor.java
 * Created on 21/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.property;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * <code>IntegerCellEditor</code>.
 * <p>
 * @author Kin Wong
 */
public class IntegerCellEditor extends TextCellEditor {

	/**
	 * Constructs an instance of <code>IntegerCellEditor</code>.
	 * <p>
	 * 
	 */
	public IntegerCellEditor() {
	}

	/**
	 * Constructs an instance of <code>IntegerCellEditor</code>.
	 * <p>
	 * @param parent
	 */
	public IntegerCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * Constructs an instance of <code>IntegerCellEditor</code>.
	 * <p>
	 * @param parent
	 * @param style
	 */
	public IntegerCellEditor(Composite parent, int style) {
		super(parent, style);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TextCellEditor#doGetValue()
	 */
	protected Object doGetValue() {
		try {
			return new Integer(text.getText());
		}
		catch(NumberFormatException e) {
			return new Integer(0);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TextCellEditor#doSetValue(java.lang.Object)
	 */

	protected void doSetValue(Object value) {
		if (text != null && (value instanceof Integer))
		{
			super.doSetValue(value.toString());
		}
	}
}
