/*
 * @(#)MeemkitLibrary.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meemkit.core;

import java.io.Serializable;

/**
 * Describes a single Meemkit library entry. The name of the library is
 * the location of the jar file relative to the Meemkit descriptor file.
 * 
 * @author mg
 */
public class MeemkitLibrary implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	private final String name;
	
	private MeemkitLibraryExport[] exports = null;
	
	public MeemkitLibrary(String name) {
		this.name = name;
	}
 	
	/**
	 * Returns the library's name.
	 * 
	 * @return Returns the library's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the library's package/class exports.
	 * 
	 * @return Returns the library's package/class exports.
	 */
	public MeemkitLibraryExport[] getExports() {
		return exports;
	}
	
	/**
	 * Sets the package/class exports for the library.
	 * 
	 * @param exports The package/class exports to set.
	 */
	public void setExports(MeemkitLibraryExport[] exports) {
		this.exports = exports;
	}
}
