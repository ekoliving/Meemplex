/*
 * @(#)InterfaceListFilter.java
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

import java.util.*;

import org.openmaji.meem.filter.Filter;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class InterfaceListFilter implements Filter {
	
	private static final long serialVersionUID = 0L;

	private Vector interfaces = new Vector();

	public InterfaceListFilter() {
	}

	public InterfaceListFilter(String interfaceName) {
		interfaces.add(interfaceName);
	}

	public void addInterface(String interfaceName) {
		interfaces.add(interfaceName);
	}

	public boolean match(String interfaceName) {
		if (interfaces.contains(interfaceName))
			return true;
		else
			return false;
	}

	public boolean match(Collection interfaceNames) {

		for (Iterator i = interfaceNames.iterator(); i.hasNext();) {
			String interfaceName = (String) i.next();
			if (interfaces.contains(interfaceName))
				return true;
		}

		return false;
	}

}
