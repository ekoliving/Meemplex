/*
 * @(#)FacetOutboundAttribute.java
 * Created on 10/07/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;


/**
 * Attributes describing an outbound facet.
 * <p>
 * @author Kin Wong
 */
public final class FacetOutboundAttribute extends FacetAttribute {
	private static final long serialVersionUID = -7438375371856493807L;

	private String wedgePublicFieldName;
	
	/**
	 * Constructs an instance of <code>FacetOutboundAttribute</code>.
	 * <p>
	 * @param identifier The identifier of this 
	 * <code>FacetOutboundAttribute</code>.
	 * @param interfaceName The interface name of this
	 * <code>FacetOutboundAttribute</code>.
	 * @param wedgePublicFieldName The wedge public field name of this
	 * <code>FacetOutboundAttribute</code>.
	 */
	public FacetOutboundAttribute(
		String identifier, String interfaceName, String wedgePublicFieldName) {
		super(identifier, interfaceName);
		this.wedgePublicFieldName = wedgePublicFieldName;
	}
	
	/**
	 * Constructs an instance of <code>FacetOutboundAttribute</code>.
	 * <p>
	 * <code>FacetOutboundAttribute</code>.
	 * @param interfaceName The interface name of this
	 * <code>FacetOutboundAttribute</code>.
	 */
	public FacetOutboundAttribute(String interfaceName, String wedgePublicFieldName) {
		this(wedgePublicFieldName, interfaceName, wedgePublicFieldName);
	}

	/**
	 * Gets the Wedge implementation public field name of this 
	 * <code>FacetOutboundAttribute</code>.
	 * <p>
	 * @return The wedge implementation public field name of this 
	 * <code>FacetOutboundAttribute</code>.
	 */
	public String getWedgePublicFieldName() {
		return wedgePublicFieldName;
	}

	/**
	 * Indicates whether the Facet <code>Direction</code> is the same as the
	 * given <code>Direction</code>.
	 * <p>
	 * @return true, if the Facet Direction is the same as the given Direction
	 */
	public boolean isDirection(Direction direction) {
		return(direction.equals(Direction.OUTBOUND));
	}

	/**
	 * Sets the Wedge implementation public field name of this 
	 * <code>FacetOutboundAttribute</code>.
	 * <p>
	 * @param wedgePublicFieldName Wedge implementation public field name of 
	 * this <code>FacetOutboundAttribute</code>.
	 */
	public void setWedgePublicFieldName(String wedgePublicFieldName) {
		this.wedgePublicFieldName = wedgePublicFieldName;	  	
	}

	/**
	 * Compares the content of <code>FacetOutboundAttribute</code>.
   * @param outboundFacetType The outbound Facet attribute
   * @return Whether it is the same
	 */
	public boolean contentEquals(FacetOutboundAttribute outboundFacetType) {
		if(!super.contentEquals(outboundFacetType)) return false;
		return getWedgePublicFieldName().
			equals(outboundFacetType.getWedgePublicFieldName());
	}

  /**
   * Provides a String representation of <code>FacetOutboundAttribute</code>.
   *
   * @return String representation of FacetOutboundAttribute
   */

  public synchronized String toString() {
    return(
      getClass().getName()      + "[" +
        "identifier="           + identifier      +
      ", interfaceName="        + interfaceName +
      ", wedgePublicFieldName=" + wedgePublicFieldName +
      "]"
    );
  }
}
