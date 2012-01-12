/*
 * @(#)MeemkitExport.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meemkit.core;

import java.io.Serializable;

/**
 * Describes a single Meemkit library export entry.
 * This value can be either:<br>
 * * - export all classes in the library<br>
 * [package name].* - export all classes within the given package in the library<br>
 * [class name] - export only the exact class specified.
 * 
 * @author mg
 */
public class MeemkitLibraryExport implements Serializable {

	private static final long serialVersionUID = 534540102928363464L;

	private final String value;
	
	public MeemkitLibraryExport(String value) {
		this.value = value;
	}
	
	/**
	 * Returns the export's value.
	 * 
	 * @return Returns the export's value.
	 */
	public String getValue() {
		return value;
	}
	
}
