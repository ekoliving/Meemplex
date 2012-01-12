/*
 * @(#)StringAddressPart.java
 *
 *  Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 *  This software is the proprietary information of Majitek Limited.
 *  Use is subject to license terms.
 */

package org.openmaji.automation.address;

/**
 * An address part used specifically to represent a string that matches
 * a regular expression mask.
 *
 * @author  mg
 * @version 1.0
 */
public class StringAddressPart extends AbstractAddressPart {
	
	private String value;
	private String validateMask = null;
	
	public StringAddressPart(String name, String description) {
		super(name, description);
	}
	
	public StringAddressPart(String name, String description, String validateMask) {
		super(name, description);
		this.validateMask = validateMask;
	}

	public String get() {
		return value;
	}

	public boolean set(String stringValue) {
		
		lastErrorMessage = null;
		
		if (validateMask == null) {
			value = stringValue;
			return true;
		}
		
		if (stringValue.matches(validateMask)) {
			value = stringValue;
			return true;
		}
		
		lastErrorMessage = "Invalid value";
		return false;
	}
}
