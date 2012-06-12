/*
 * @(#)FacetDefinition.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.meem.definition;

import java.io.Serializable;


/**
 * <p>
 * The FacetDefinition defines a Facet's attributes.
 * </p>
 * <p>
 * The key attributes of a Facet are its interface name and identifier.
 * A Facet's other attributes depend upon whether is in-bound or out-bound.
 * <p>
 * Since the FacetDefinition is part of a deeper definition structure,
 * it is used only in those occasions when the whole thing is needed.
 * </p>
 * <p>
 * Circumstances that require individual manipulation of parts of the
 * Facet definition structure, such as MetaMeem clients, should instead
 * utilize the MeemStructure mechanism.
 * </p>
 * <p>
 * The FacetDefinition describes a Java interface type that can considered to
 * be either in-bound or out-bound (not both).  An in-bound FacetDefinition
 * interface maps Meem method invocations to a given Wedge implementation.
 * An out-bound FacetDefinition interface applies to a public field in
 * the Wedge implementation class.  Method invocations can be made on the
 * object reference defined by that public field, which will result in
 * method invocations being attempted on other Meems.  It is recommended
 * (not required) that the default direction is "in-bound".
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-07-28)
 * </p>
 * @author  Andy Gelme
 * @author  MG
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.DependencyAttribute
 * @see org.openmaji.meem.definition.FacetAttribute
 */

public final class FacetDefinition implements Serializable {
	private static final long serialVersionUID = -7927851784617829373L;

  /**
   * Attributes of the Facet
   */

  private FacetAttribute facetAttribute;

  /**
   * Create a FacetDefinition.
   *
   * @param facetAttribute Attributes of the Facet
   */

  public FacetDefinition(
    FacetAttribute facetAttribute) {

    if (facetAttribute == null) {
      throw new IllegalArgumentException("FacetAttribute must be provided");
    }

    this.facetAttribute = facetAttribute;
  }

  /**
   * Provides the FacetAttribute.
   *
   * @return Attributes of the Facet
   */

  public FacetAttribute getFacetAttribute() {
    return(facetAttribute);
  }

  /**
   * Assigns the FacetAttribute.
   *
   * @param facetAttribute Attributes of the Facet
   */

  public void setFacetAttribute(
    FacetAttribute facetAttribute) {

    this.facetAttribute = facetAttribute;
  }

  /**
   * Compares FacetDefinition to the specified object.
   * The result is true, if and only if the FacetAttributes are equal
   * and the DependencyAttributes are equal.
   *
   * @return true if FacetDefinitions are equal
   */

  public synchronized boolean equals(
    Object object) {

    if (object == this) return(true);

    if ((object instanceof FacetDefinition) == false) return(false);

    FacetDefinition thatFacetDefinition = (FacetDefinition) object;

    boolean match = false;

    match = facetAttribute.equals(thatFacetDefinition.getFacetAttribute());

    return(match);
  }

  /**
   * Provides the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   *
   * @return FacetDefinition hashCode
   */

  public synchronized int hashCode() {
    int hashCode = facetAttribute.hashCode();

    return(hashCode);
  }
  
  public FacetDefinition clone() {
	  FacetAttribute copyFacetAttribute = facetAttribute.clone();
	  return new FacetDefinition(copyFacetAttribute);
  }

  /**
   * Provides a String representation of FacetDefinition.
   *
   * @return String representation of FacetDefinition
   */

  public synchronized String toString() {
    return(
      getClass().getName()     + "[" +
        "facetAttribute="      + facetAttribute      +
      "]"
    );
  }
}
