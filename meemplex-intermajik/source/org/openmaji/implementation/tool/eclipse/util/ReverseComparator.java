/*
 * @(#)ReverseComparator.java
 * Created on 16/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * <code>ReverseComparator</code> reverses the results of another comparator.
 * <p>
 * @author Kin Wong
 */
public class ReverseComparator implements Comparator, Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	private Comparator comparator;
	/**
	 * Constructs an instance of <code>ReverseComparator</code>.
	 * <p>
	 * @param comparator The comparator to be reversed.
	 */
	public ReverseComparator(Comparator comparator) {
		this.comparator = comparator;		
	}
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		int cmp = comparator.compare(o1, o2);
		return -(cmp | (cmp >>> 1));
	}
}
