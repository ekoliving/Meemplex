/*
 * @(#)Pair.java
 * Created on 05/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.util;

import java.io.Serializable;

/**
 * <code></code>Pair can be used to associate two object as a unit.
 * <p>
 * @author Kin Wong
 */
public class Pair implements Cloneable, Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	private Object first;	// The first object in the pair
	private Object second;	// The second object in the pair
	
	/**
	 * Constructs an instance of a <code>Pair</code>.
	 * <p>
	 */
	public Pair() {
	}

	/**
	 * Constructs an instance of a pair.
	 * <p>
	 * @param first The first object of this pair.
	 * @param second the second object of this pair.
	 */
	public Pair(Object first, Object second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Gets the first object of this <code>Pair</code>.
	 * <p>
	 * @return Object The first object of this <code>Pair</code>.
	 */
	public Object getFirst() {
		return first;
	}
	
	/**
	 * Gets the second object of this <code>Pair</code>.
	 * <p>
	 * @return Object The second object of this <code>Pair</code>.
	 */
	public Object getSecond() {
		return second;
	}
	
	/**
	 * Sets the first object of this <code>Pair</code>.
	 * <p>
	 * @param first The first object of this <code>Pair</code>.
	 */
	public void setFirst(Object first) {
		this.first = first;
	}
	
	/**
	 * Sets the second object of this <code>Pair</code>.
	 * <p>
	 * @param second The second object of this <code>Pair</code>.
	 */
	public void setSecond(Object second) {
		this.second = second;
	}
	
	/**
	 * returns the hash code of this <code>Pair</code>.
	 * <p>
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}
	
	/**
	 * Compares an object with this <code>Pair</code>.
	 * <p>
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof Pair)) return false;
		Pair that = (Pair)obj;
		return first.equals(that.getFirst()) && second.equals(that.getSecond());
	}
	
	/**
	 * Returns a <code>String</code> that represents this <code>Pair</code>.
	 * <p>
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getFirst().toString() + "," + getSecond().toString();
	}
}
