/*
 * @(#)Direction.java
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
 * The Direction represents the information flow direction for a Meem Facet.
 * A Direction can be either "in-bound" or "out-bound" (not both).
 * </p>
 * <p>
 * An in-bound Facet provides a Java interface for method invocations made
 * on a specified Wedge implementation within a given Meem.
 * </p>
 * <p>
 * An out-bound Facet interface applies to a public field in a Wedge
 * implementation class.  Method invocations can be made on the object
 * reference defined by that public field, which will result in method
 * invocations being attempted on other Meems.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-03-12)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.definition.FacetDefinition
 */

public final class Direction implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

  /**
   * Direction for in-bound Facet method invocations to a Meem
   */

  public static final Direction INBOUND = new Direction("Inbound");

  /**
   * Direction for out-bound Facet method invocations from a Meem
   */

  public static final Direction OUTBOUND = new Direction("Outbound");

  /**
   * Uniquely distinguishes one Direction from another
   */

  private String identifier;

  /**
   * Create Direction.
   *
   * @param identifier Unique Direction distinguisher
   */

  public Direction(
    String identifier) {

    this.identifier = identifier;
  }

  /**
   * Returns the identifier that this Direction represents
   *
   * @return String The identifier of the Direction
   */

  public String getIdentifier() {
    return(identifier);
  }

  /**
   * Compares Direction to the specified object.
   * The result is true, if and only if both Direction's identifiers are equal.
   *
   * @return true if Directions are equal
   */

  public boolean equals(
    Object object) {

    if (object == this) return(true);

    if (object instanceof Direction) {
      Direction thatDirection = (Direction) object;

      return(identifier.equals(thatDirection.getIdentifier()));
    }

    return(false);
  }

  /**
   * Provide a hash code for this Direction.
   * The Direction hash code is equivalent to the Direction's identifier
   * hash code.
   *
   * @return Hash code value for this Direction
   */

  public int hashCode() {
    return(identifier.hashCode());
  }

  /**
   * Provides a String representation of Direction.
   *
   * @return String representation of Direction
   */

  public String toString() {
    return("[" + identifier + "]");
  }
}
