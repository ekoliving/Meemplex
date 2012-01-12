/*
 * @(#)LifeTime.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.definition;

import java.io.Serializable;

/**
 * <p>
 * Dependency life time.
 * </p>
 * @author  mg
 * @version 1.0
 */
public final class LifeTime implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	/**
	 * Needs to be persisted
	 */

	public static final LifeTime PERMANENT = new LifeTime("permanent");

	/**
	 * Must not be persisted
	 */

	public static final LifeTime TRANSIENT = new LifeTime("transient");

	/**
	 * Uniquely distinguishes one LifeTime from another
	 */

	private String identifier;

	/**
	 * Create LifeTime.
	 *
	 * @param identifier Unique LifeTime distinguisher
	 */

	public LifeTime(
		String identifier) {

		this.identifier = identifier;
	}

	/**
	 * Returns the identifier that this LifeTime represents
	 *
	 * @return String The identifier of the LifeTime
	 */

	public String getIdentifier() {
		return(identifier);
	}

	/**
	 * Compares LifeTime to the specified object.
	 * The result is true, if and only if both LifeTime's identifiers are equal.
	 *
	 * @return true if LifeTimes are equal
	 */

	public boolean equals(
		Object object) {

		if (object == this) return(true);

		if (object instanceof LifeTime) {
			LifeTime thatLifeTime = (LifeTime) object;

			return(identifier.equals(thatLifeTime.getIdentifier()));
		}

		return(false);
	}

	/**
	 * Provide a hash code for this LifeTime.
	 * The LifeTime hash code is equivalent to the LifeTime's identifier
	 * hash code.
	 *
	 * @return Hash code value for this LifeTime
	 */

	public int hashCode() {
		return(identifier.hashCode());
	}

	/**
	 * Provides a String representation of LifeTime.
	 *
	 * @return String representation of LifeTime
	 */

	public String toString() {
		return("[" + identifier + "]");
	}
}
