/*
 * @(#)DependencyType.java
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
 * The DependencyType defines the functional operation of a given Dependency.
 * </p>
 * <p>
 * The defined DependencyTypes are ...
 * <ul>
 *   <li>STRONG      - Meem is ready only when the Dependency is resolved
 *                     on another individual Meem.</li>
 *
 *   <li>WEAK        - Meem ready regardless of when the Dependency is resolved
 *                     on another individual Meem.</li>
 *
 *   <li>STRONG_MANY - Meem is ready only when the Dependency is resolved
 *                     on the multiple Meems contained by a Category.</li>
 *
 *   <li>WEAK_MANY   - Meem ready regardless of when the Dependency is resolved
 *                     on the multiple Meems contained by a Category.</li>
 * </ul>
 * </p>
 * <p>
 * The first pair of DependencyTypes, "strong" and "weak", are for simple
 * resolution of Dependencies.  There will be a one-to-one relationship
 * established between the Meems, regardless of the type of the provider
 * Meem.  A "strong" Dependency effects the LifeCycle of the client Meem,
 * such that the Dependency must be resolved and the target Meem reference
 * bound, before the client Meem's LifeCycle state can be made "ready".
 * A "weak" Dependency can be resolved / bound and unresolved / unbound,
 * without affecting the client Meem's LifeCycle state.
 * Typically, these DependencyTypes will be used by client Meems that need
 * to browse or manipulate Category Meems and their Entries.
 * </p>
 * <p>
 * The second pair of DependencyTypes, STRONG_MANY and WEAK_MANY, are
 * for more sophisticated resolution of Dependencies.  There can be a
 * one-to-many relationship between one client Meem and many provider
 * Meems.  If the target Meem is a Category, then the Category Entries
 * are used as MeemPaths, which are then resolved as a group of Meems
 * that is to be depended upon.  Otherwise, if the target Meem is not
 * a Category, then a simple one-to-one relationship is the result.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-03-19)
 * </p>
 * @author  Andy Gelme
 * @author  MG
 * @version 1.0
 * @see org.openmaji.meem.definition.DependencyAttribute
 */

public final class DependencyType implements Serializable {
	private static final long serialVersionUID = 3670645886376153023L;

  /**
   * Meem ready when Dependency resolved, one-to-one relationship.
   */

  public static final DependencyType STRONG = new DependencyType("strong");

  /**
   * Meem ready regardless of Dependency resolution, one-to-one relationship.
   */

  public static final DependencyType WEAK = new DependencyType("weak");

  /**
   * Meem ready when Dependency resolved, one-to-many relationship.
   */

  public static final DependencyType STRONG_MANY =
    new DependencyType("strongMany");

  /**
   * Meem ready regardless of Dependency resolution, one-to-many relationship.
   */

  public static final DependencyType WEAK_MANY =
    new DependencyType("weakMany");

  /**
   * Uniquely distinguishes one DependencyType from another
   */

  private String identifier;

  /**
   * Create DependencyType.
   *
   * @param identifier Unique DependencyType distinguisher
   */

  private DependencyType(
    String identifier) {

    this.identifier = identifier;
  }

  /**
   * Returns the identifier that this DependencyType represents
   *
   * @return String The identifier of the DependencyType
   */

  public String getIdentifier() {
    return(identifier);
  }

  /**
   * Compares DependencyType to the specified object.
   * The result is true, if and only if both DependencyType's identifiers
   * are equal.
   *
   * @return true if DependencyTypes are equal
   */

  public boolean equals(
    Object object) {

    if (object == this) return(true);

    if (object instanceof DependencyType) {
      DependencyType thatDependencyType = (DependencyType) object;

      return(identifier.equals(thatDependencyType.getIdentifier()));
    }

    return(false);
  }

  /**
   * Provide a hash code for this DependencyType.
   * The DependencyType hash code is equivalent to the DependencyType's
   * identifier hash code.
   *
   * @return Hash code value for this DependencyType
   */

  public int hashCode() {
    return(identifier.hashCode());
  }

  /**
   * Provides a String representation of DependencyType.
   *
   * @return String representation of DependencyType
   */

  public String toString() {
    return("[" + identifier + "]");
  }
}
