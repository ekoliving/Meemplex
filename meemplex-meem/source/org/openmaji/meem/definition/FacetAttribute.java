/*
 * @(#)FacetAttribute.java
 * Created on 9/07/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;


import java.io.Serializable;

/**
 * <code>FacetAttribute</code>.
 * <p>
 * @author Kin Wong
 */

abstract public class FacetAttribute implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 0L;

	protected String identifier;	// The user-defined name of the facet
	protected String interfaceName;	// The Java interface name associates with this facet
	
	/**
	 * Constructs an instance of <code>FacetAttribute</code>.
	 * <p>
	 */
	protected FacetAttribute(String identifier, String interfaceName) {
		
		this.identifier = identifier;
		this.interfaceName = interfaceName;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try { 
			return super.clone();
		} catch (CloneNotSupportedException e) { 
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Returns the identifier of this <code>FacetAttribute</code>. The identifier
   * can be used to uniquely identify a facet within a Meem.
	 * <p>
	 * @return The identifier of this <code>FacetAttribute</code>.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets the interface type name of this <code>FacetAttribute</code>.
	 * <p>
	 * @return The Interface type name of this <code>FacetAttribute</code>.
	 */
	public String getInterfaceName() {
		return interfaceName;
	}

	/**
	 * Indicates whether the Facet <code>Direction</code> is the same as the
     * given <code>Direction</code>.
	 * <p>
	 * @return true, if the Facet Direction is the same as the given Direction, 
	 * false otherwise.
	 */
	public abstract boolean isDirection(Direction direction);

	/**
	 * Sets the Facet name of this <code>FacetAttribute</code>.
	 * <p>
	 * The facet name is an user-defined name of the <code>FacetAttribute</code>.
	 * @param name The name of the FacetAttribute.
	 */
	public void setIdentifier(String name) {
		this.identifier = name;	
	}

	/**
	 * Sets the Facet interface type name.
	 * <p>
	 * @param interfaceName Facet's interface type name
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * Compares <code>FacetAttribute</code> to the specified object.
	 */
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object.getClass().equals(this.getClass()) == false) {
			return false;
		}
		FacetAttribute that = (FacetAttribute) object;
		if (that.identifier.equals(this.identifier) == false) {
			return false;
		}
		return true;
	}
	
	/**
	 * Compares the content of <code>FacetAttribute</code>s.
	 * <p>
	 * @param   facetType The <code>FacetAttribute</code> to compare to.
	 * @return Returns <code>true</code> if and only if all fields are 
	 * identical, otherwise <code>false</code>.
	 * @see #equals(Object)
	 * @see #hashCode()
	 */
	public boolean contentEquals(FacetAttribute facetType) {
//System.err.println("FacetAttribute.contentEquals() this=["+this+"] that=["+facetType+"]");
    if(this == facetType) return true;
		if(facetType == null) return false;
		if(!getIdentifier().equals(facetType.getIdentifier())) return false;
		return (getInterfaceName().equals(facetType.getInterfaceName())); 
	}
	
	/**
	 * Returns a hash code for this <code>FacetAttribute</code>.
	 */
	public int hashCode() {
    int hash = 7;
    hash = 31 * hash + ( identifier == null ? 0 : identifier.hashCode() );
    return hash;
	}

  /**
   * Provides a String representation of <code>FacetAttribute</code>.
   *
   * @return String representation of FacetAttribute
   */

  public synchronized String toString() {
    return(
      getClass().getName() + "[" +
        "identifier="    + identifier      +
      ", interfaceName=" + interfaceName +
      "]"
    );
  }
}
