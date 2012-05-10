/*
 * @(#)MeemComparator.java
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

import org.openmaji.meem.Meem;


/**
 * Used to compare two Meems. It basically does a String comparison of the two Meems MeemPaths
 * @author  mg
 * @version 1.0
 */
public class MeemComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if ((o1 instanceof Meem) && (o2 instanceof Meem)) {
			String meemPath1 = ((Meem)o1).getMeemPath().toString();
			String meemPath2 = ((Meem)o2).getMeemPath().toString();
			
			return meemPath1.compareTo(meemPath2);
		}
		throw new ClassCastException();
	}

}
