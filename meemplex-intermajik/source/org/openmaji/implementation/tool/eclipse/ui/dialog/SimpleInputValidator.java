/*
 * @(#)SimpleInputValidator.java
 * Created on 21/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.dialog;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * <code>SimpleInputValidator</code>.
 * <p>
 * @author Kin Wong
 */
public class SimpleInputValidator implements IInputValidator {
	private boolean caseSensitive;
	private String value;
	private String message = "The value has not been changed";
	/**
	 * Constructs an instance of <code>SimpleInputValidator</code>.
	 * <p>
	 * 
	 */
	public SimpleInputValidator(boolean caseSensitive, String message, String value) {
		this.caseSensitive = caseSensitive;
		this.message = message;
		this.value = value;
	}
	
	public SimpleInputValidator(String message, String value) {
		this(false, message, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
	 */
	public String isValid(String newText) {
		if(caseSensitive) {
			if(0 != newText.compareTo(value)) return null;
		}
		else {
			if(0 != newText.compareToIgnoreCase(value)) return null;
		}
		return message;
	}
}
