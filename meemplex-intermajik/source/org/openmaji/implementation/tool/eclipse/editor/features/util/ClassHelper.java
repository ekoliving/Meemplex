/*
 * @(#)ClassHelper.java
 * Created on 5/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.util;

/**
 * <code>ClassHelper</code>.
 * <p>
 * @author Kin Wong
 */
public class ClassHelper {
	static public String getClassNameFromFullName(String fullName) {
		String className = fullName;
		int index = fullName.lastIndexOf('.');
		if(index != -1) 
		className = fullName.substring(index + 1);
		return className;
	}
}
