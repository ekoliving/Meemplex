/*
 * @(#)MeemContext.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.meem;

/**
 * <p>
 * Context information applicable to an application wedge.
 * </p>
 */

public interface MeemContext {

	/**
	 * Return the Meem that the declaring Wedge is part of.
	 */
	public Meem getSelf();

	/**
	 * Return the identifier for the Wedge this meemContext is in.
	 * 
	 * @return the wedgeIdentifier
	 */
	public String getWedgeIdentifier();

	/**
	 * Return the inbound facet named by the <code>facetIdentifier</code> parameter.
	 * 
	 * @return target proxy facet.
	 */
	public Facet getTarget(String facetIdentifier);

	/**
	 * Generate a target proxy for a delegate facet adding an implementation of Meem in addition to the classes represented by facet and specification.
	 * 
	 * @param facet
	 * @param specification
	 * @return target proxy facet
	 */
	public <T extends Facet> T  getTargetFor(T facet, Class<T> specification);

	/**
	 * Generate a target proxy for a delegate facet. In this case the implementation is limited to the classes represented by facet and specification. Meem is not implemented
	 * unless explicitly provided.
	 * 
	 * @param facet
	 * @param specification
	 * @return target A proxy for
	 */
	public <T extends Facet> T getLimitedTargetFor(T facet, Class<T> specification);

	/**
	 * Generate a target proxy for a delegate facet. In this case the implementation is limited to the classes represented by facet and specification. Meem is not implemented
	 * unless explicitly provided. No decoupling will take place on calls on this target.
	 * 
	 * @param facet
	 * @param specification
	 * @return target proxy facet
	 */
	public <T extends Facet> T  getNonBlockingTargetFor(T facet, Class<T> specification);

	/**
	 * Provide the value of an ImmutableAttribute.
	 * 
	 * @param key
	 *            Index for the required ImmutableAttribute value
	 * @return ImmutableAttribute value for the given key
	 * @exception IllegalArgumentException
	 *                ImmutableAttribute key isn't valid
	 */
	public Object getImmutableAttribute(Object key) throws IllegalArgumentException;
}