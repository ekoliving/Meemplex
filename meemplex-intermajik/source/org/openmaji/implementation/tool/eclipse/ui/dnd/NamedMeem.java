/*
 * @(#)NamedMeem.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dnd;

import java.io.Serializable;

import org.openmaji.meem.MeemPath;


/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class NamedMeem implements Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	private final String name;
	private final MeemPath meemPath;
	
	/**
	 * Constructs an instance of <code>NamedMeem</code>.
	 * <p>
	 * @param name
	 * @param meemPath
	 */
	public NamedMeem(String name, MeemPath meemPath) {
		this.name = name;
		this.meemPath = meemPath;
	}
	
	public String getName() {
		return name;
	}
	
	public MeemPath getMeemPath() {
		return meemPath;
	}
}
