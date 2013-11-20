/*
 * @(#)FacetInboundAttribute.java
 * Created on 10/07/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;


/**
 * Attributes describing an inbound facet.
 * <p>
 * @author Kin Wong
 */
public final class FacetInboundAttribute extends FacetAttribute {
	private static final long serialVersionUID = -1064440345945763624L;

	static private final boolean DEFAULT_CONTENTREQUIRED = true;
	
	private boolean contentRequired;
	/**
	 * Constructs an instance of <code>FacetInboundAttribute</code>.
	 * <p>
	 * @param identifier The identifier of this 
	 * <code>FacetInboundAttributes</code>.
	 * @param interfaceName The interface name of this 
	 * <code>FacetInboundAttributes</code>.
	 * @param contentRequired Indicates whether this in-bound Facet requires 
	 * content upon resolving a dependency.
	 */
	public FacetInboundAttribute(
		String identifier, String interfaceName, boolean contentRequired) {
		super(identifier, interfaceName);
		this.contentRequired = contentRequired;
	}
	
	/**
	 * Constructs an instance of <code>FacetInboundAttribute</code>.
	 * <p>
	 * @param identifier The identifier of this 
	 * <code>FacetInboundAttributes</code>.
	 * @param interfaceName The interface name of this 
	 * <code>FacetInboundAttributes</code>.
	 */
	public FacetInboundAttribute(String identifier, String interfaceName) {
		this(identifier, interfaceName, DEFAULT_CONTENTREQUIRED);
	}
	
	/**
	 * Indicates whether this in-bound Facet requires content upon resolving
	 * a dependency.
	 * <p>
	 * @return <code>true</code> if content is required, otherwise false.
	 */
	public boolean isContentRequired() {
		return contentRequired;
	}

	/**
	 * Indicates whether the Facet <code>Direction</code> is the same as the
	 * given <code>Direction</code>.
	 * <p>
	 * @return true, if the Facet Direction is the same as the given Direction
	 */
	public boolean isDirection(Direction direction) {
		return(direction.equals(Direction.INBOUND));
	}
	
	/**
	 * Sets whether this in-bound Facet requires content upon resolving
	 * a dependency.  
	 * <p>
	 * @param contentRequired Out-bound Facet dependency sends initial Content.
	 */
	public void setContentRequired(boolean contentRequired) {
		this.contentRequired = contentRequired;
	}
	
	/**
	 * Compares the content of this <code>FacetInboundAttribute</code>.
   * 
   * @param inboundFacetType The inbound Facet attribute
   * @return Whether it is the same
	 */
	public boolean contentEquals(FacetInboundAttribute inboundFacetType) {
		if(!super.contentEquals(inboundFacetType)) return false;
		return isContentRequired() == inboundFacetType.isContentRequired();
	}

  /**
   * Provides a String representation of <code>FacetInboundAttribute</code>.
   *
   * @return String representation of FacetInboundAttribute
   */

  public synchronized String toString() {
    return(
      getClass().getName() + "[" +
        "identifier="      + identifier +
      ", interfaceName="   + interfaceName +
      ", contentRequired=" + contentRequired +
      "]"
    );
  }
  
  public FacetInboundAttribute clone() {
	  return new FacetInboundAttribute(identifier, interfaceName, contentRequired);
  }
}
