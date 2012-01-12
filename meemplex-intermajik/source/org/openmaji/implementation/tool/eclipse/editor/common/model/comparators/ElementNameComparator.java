/*
 * @(#)ElementNameComparator.java
 * Created on 21/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.model.comparators;

import java.util.Comparator;

import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;


/**
 * <code>ElementNameComparator</code> implements the <code>Comparator</code> 
 * by returning the compare return of the element names.
 * <p>
 * @author Kin Wong
 */
public class ElementNameComparator implements Comparator {
	private boolean caseSensitive;
	/**
	 * Constructs an instance of <code>ElementNameComparator</code>.
	 * <p>
	 * @param caseSensitive Whether comparison is performed case-sensitively.
	 */
	public ElementNameComparator(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	/**
	 * Constructs an instance of <code>ElementNameComparator</code> and default
	 * the comparison to case-sensitive.
	 * <p>
	 */
	public ElementNameComparator() {
		caseSensitive = true;
	}
	/**
	 * Gets whether comparison is case-sensitive.
	 * @return boolean true if the comparison is case-sensitive, false 
	 * otherwise.
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	/**
	 * Sets whether comparison is case-sensitive.
	 * @param caseSensitive true if the comparison is to be case-sensitive, 
	 * false otherwise.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Element element1 = (Element)o1;
		Element element2 = (Element)o2;
		if(caseSensitive)
		return element1.getName().compareTo(element2.getName());
		else
		return element1.getName().compareToIgnoreCase(element2.getName());
	}
}
