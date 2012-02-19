/*
 * @(#)StringMatchInputValidator.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dialog;

import java.util.Collection;

import org.eclipse.jface.dialogs.IInputValidator;

/**
    * <p>
    * Simple duplicate string validator.
    * </p>
    * @author  mg
    * @version 1.0
    */
public class DuplicateInputValidator implements IInputValidator {
	
	Collection strings;
	String errorMessage;

	public DuplicateInputValidator(Collection strings, String errorMessage) {
		this.strings = strings;
		this.errorMessage = errorMessage;
	}

	/**
	 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
	 */
	public String isValid(String newText) {
		if (strings.contains(newText))
			return errorMessage;
		return null;
	}

}
