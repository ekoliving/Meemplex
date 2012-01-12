/*
 * @(#)SortRequest.java
 * Created on 16/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.sorting;

import java.util.Comparator;

import org.eclipse.gef.Request;

/**
 * <code>SortRequest</code> represents a request to sort items in a container by
 * the accompany <code>Comparator</code>.
 * <p>
 * @author Kin Wong
 */
public class SortRequest extends Request {
	private Comparator comparator;
	/**
	 * Constructs an instance of <code>SortRequest</code>.
	 * <p>
	 * @param comparator The <code>Comparator</code> that defines the sort 
	 * order.
	 */
	public SortRequest(Comparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * Get the Comparator that defines the sort order.
	 * @return Comparator The comparator that defines the sort order.
	 */
	public Comparator getComparator() {
		return comparator;
	}
	
	/**
	 * Sets the comparator that defines the sort order.
	 * @param comparator The comparator that defines the sort order.
	 */
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
}
