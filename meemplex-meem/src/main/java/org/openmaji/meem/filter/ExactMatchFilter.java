/*
 * @(#)ExactMatchFilter.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.filter;

import java.io.Serializable;

/**
 * <p>
 * An ExactMatchFilter simply checks that a specified Object is equal to the
 * Object expected.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-01-08)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.filter.Filter
 * @see org.openmaji.meem.filter.FilterChecker
 */

public final class ExactMatchFilter<T extends Serializable> implements Filter, Serializable {
	private static final long serialVersionUID = -1178365040590887015L;

	/**
	 * Object to be compared
	 */

	private T template;

	public static <S extends Serializable> ExactMatchFilter<S> create(S template) {
		return new ExactMatchFilter<S>(template);
	}

	/**
	 * <p>
	 * Create ExactMatchFilter.
	 * </p>
	 * 
	 * @param template
	 *            Object to be compared
	 */

	protected ExactMatchFilter(T template) {
		this.template = template;
	}

	/**
	 * Provide Template object to be compared for ExactMatchFilter.
	 * 
	 * @return Template object to be compared for ExactMatchFilter
	 */

	public T getTemplate() {
		return (template);
	}

	/**
	 * <p>
	 * Comparision with the specified Object. The result is true, if and only if
	 * both Objects are equal.
	 * </p>
	 * 
	 * @param target
	 *            Object to be compared
	 * @return True, if Objects are equal
	 */

	public boolean equals(Object target) {

		if (this == target)
			return true;

		if (target instanceof ExactMatchFilter) {
			ExactMatchFilter<?> filter = (ExactMatchFilter<?>) target;
			return (template.equals(filter.getTemplate()));
		}

		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return 58 ^ template.hashCode();
	}

	/**
	 * <p>
	 * Provides a String representation of ExactMatchFilter.
	 * </p>
	 * 
	 * @return String representation of ExactMatchFilter
	 */

	public String toString() {
		return (getClass().getName() + "[" + "template=" + template + "]");
	}
}
