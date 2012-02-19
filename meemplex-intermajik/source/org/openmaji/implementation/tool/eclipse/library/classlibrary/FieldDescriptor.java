/*
 * @(#)FieldDescriptor.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.library.classlibrary;

/**
 * @author mg
 * Created on 17/01/2003
 */
public class FieldDescriptor {
	private String name;
	private String type;
	
	public FieldDescriptor(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type.
	 * @return String
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		return name;
	}

}
