/*
 * @(#)MeemPathComparator.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.util;

import java.util.Comparator;

import org.openmaji.meem.MeemPath;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemPathComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if ((o1 instanceof MeemPath) && (o2 instanceof MeemPath)) {
			String meemPath1 = ((MeemPath)o1).toString();
			String meemPath2 = ((MeemPath)o2).toString();
			
			return meemPath1.compareTo(meemPath2);
		}
		throw new ClassCastException();
	}

}
