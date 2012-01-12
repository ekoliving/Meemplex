/*
 * @(#)FacetImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.meem;

import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.FacetAttribute;

/**
 * <p>
 * This is the run-time model for a Meem Facet instance.
 * </p>
 * <p>
 * The FacetImpl maintains "Meem instance" information used at run-time and does not duplicate any of the information already contained in the FacetAttribute.
 * </p>
 * <p>
 * A FacetImpl consists of a reference to the FacetAttribute, the Facet interface and the Wedge implementation utilizing this Facet.
 * </p>
 * <p>
 * Out-bound FacetImpls can also provide a Dynamic Proxy Object "meemInvocationSource", which contains References to other Meems. The meemInvocationSource allows the Feature
 * invocation mechanism to intercept every out-bound Facet method call.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-04-01)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.FacetAttribute
 */

public abstract class FacetImpl <T extends Facet> {

	/**
	 * Facet interface type
	 */
	private final Class<T> specification;

	/**
	 * Wedge implementation utilizing this Facet
	 */
	private WedgeImpl wedgeImpl;

	/**
	 * Create a FacetImpl.
	 * 
	 * @param wedgeImpl
	 *            Wedge implementation utilizing this Facet
	 * @exception IllegalArgumentException
	 *                Problem loading the Facet interface
	 */

	public FacetImpl(WedgeImpl wedgeImpl, Class<T> specification) throws IllegalArgumentException {
		this.wedgeImpl = wedgeImpl;
		this.specification = specification;
	}

	public abstract FacetAttribute getFacetAttribute();

	public abstract Direction getDirection();

	public abstract T makeProxy();

	/**
	 * Provides the Facet identifier.
	 * 
	 * @return Facet identifier
	 */
	public String getIdentifier() {
		return getFacetAttribute().getIdentifier();
	}

	/**
	 * Provides the Facet interface type.
	 * 
	 * @return Facet interface type
	 */
	public Class<T> getSpecification() {
		return specification;
	}

	/**
	 * Provides the Wedge implementation utilizing this Facet.
	 * 
	 * @return Wedge implementation utilizing this Facet
	 */
	public WedgeImpl getWedgeImpl() {
		return wedgeImpl;
	}

	/**
	 * Compares FacetImpl to the specified object. The result is true, if and only if the FacetAttribute, specication and WedgeImplementation match.
	 * 
	 * Note: The meemInvocationSource is deliberately left out of equals().
	 * 
	 * @return true if FacetImpls are equal
	 */
	public synchronized boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof FacetImpl))
			return false;

		try {
			FacetImpl<T> thatFacetImpl = (FacetImpl<T>) object;
			if (!wedgeImpl.equals(thatFacetImpl.getWedgeImpl()))
				return false;
			if (!specification.equals(thatFacetImpl.getSpecification()))
				return false;
			return getFacetAttribute().equals(thatFacetImpl.getFacetAttribute());
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Provides the Object hashCode. Must follow the Object.hashCode() and Object.equals() contract.
	 * 
	 * Note: The meemInvocationSource is deliberately left out of hashCode().
	 * 
	 * @return FacetImpl hashCode
	 */
	public synchronized int hashCode() {
		return getFacetAttribute().hashCode() ^ specification.hashCode() ^ wedgeImpl.hashCode();
	}

	/**
	 * Provides a String representation of the FacetImpl.
	 * 
	 * @return String representation of the FacetImpl
	 */
	public synchronized String toString() {
		return getClass().getName() + "[" + "identifier=" + getIdentifier() + ", direction=" + getDirection() + ", specification=" + specification + "]";
	}
}
