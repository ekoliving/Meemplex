/*
 * @(#)ListAddressPart.java
 *
 *  Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 *  This software is the proprietary information of Majitek Limited.
 *  Use is subject to license terms.
 */

package org.openmaji.automation.address;

import java.util.Arrays;
import java.util.List;

/**
 * An address part used specifically to represent a string from a list
 * of allowed values.
 *
 * @author  mg
 * @version 1.0
 */
public class ListAddressPart extends AbstractAddressPart {

	private String value = null;
	private List<String> allowedValuesList;

	public ListAddressPart(String name, String description, String[] allowedValues) {
		super(name, description);
		allowedValuesList = Arrays.asList(allowedValues);
	}

	public String get() {
		return value;
	}

	public boolean set(String stringValue) {

		lastErrorMessage = null;

		if (allowedValuesList.contains(stringValue)) {
			value = stringValue;
			return true;
		}

		lastErrorMessage = "Invalid value";
		return false;
	}

	public String[] getAllowedValuesList() {
		return (String[]) allowedValuesList.toArray(new String[0]);
	}

}
