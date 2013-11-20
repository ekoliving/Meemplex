/*
 * @(#)IntegerAddressPart.java
 *
 *  Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 *  This software is the proprietary information of EkoLiving Pty Ltd.
 *  Use is subject to license terms.
 */

package org.openmaji.automation.address;

/**
 * An address part used specifically to represent an integer within a
 * specific range.
 *
 * @author  mg
 * @version 1.0
 */
public class IntegerAddressPart extends AbstractAddressPart{

	private int value = 0;
	private int lowerLimit = Integer.MIN_VALUE;
	private int upperLimit = Integer.MAX_VALUE;
	
	public IntegerAddressPart(String name, String description) {
		super(name, description);
	}
	
	public IntegerAddressPart(String name, String description, int lowerLimit, int upperLimit) {
		super(name, description);
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}

	public String get() {
		return Integer.toString(value);
	}

	public boolean set(String stringValue) {
		lastErrorMessage = null;
		
		int newValue;
		try {
			newValue = Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			return false;
		}
		
		if (newValue >= lowerLimit && newValue <= upperLimit) {
			value = newValue;
			return true;
		}
		
		lastErrorMessage = "Invalid value";
		
		return false;
	}

}
