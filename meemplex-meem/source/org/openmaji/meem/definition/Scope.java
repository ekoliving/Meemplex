/*
 * @(#)Scope.java
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
 * The Scope represents the registration visibility for a Meem.
 * </p>
 * <p>
 * When a Meem is registered with the MeemRegistry, then the Meem's Scope
 * determines the extent to which a Meem can be located by either Meems.
 * There are four Maji platform Scopes defined ...
 * <ul>
 *   <li>FEDERATED   - Visible across a WAN</li>
 *   <li>DISTRIBUTED - Visible within a LAN</li>
 *   <li>LOCAL       - Visible within a JVM</li>
 *   <li>MEEMPLEX    - Visible within a MeemPlex</li>
 * </ul>
 * </p>
 * When a Meem is attempting to locate another Meem using the MeemRegistry,
 * the extent of the search can be determined by providing a Scope.
 * <p>
 * Note: Implementation thread safe = Yes (2003-03-12)
 * </p>
 * @author  Andy Gelme
 * @author  MG
 * @version 1.0
 */

public final class Scope implements Serializable {
	private static final long serialVersionUID = -4113658061509093069L;

  /**
   * Meem may be located across Wide Area Networks
   */

  public static final Scope FEDERATED = new Scope("federated");

  /**
   * Meem may be located within Local Area Networks
   */

  public static final Scope DISTRIBUTED = new Scope("distributed");

  /**
   * Meem may be located within a single JVM
   */

  public static final Scope LOCAL = new Scope("local");

  /**
   * Meem may be located within a MeemPlex
   */

  public static final Scope MEEMPLEX = new Scope("meemplex");

  /**
   * Uniquely distinguishes one Scope from another
   */

  private String identifier;

  /**
   * Create Scope.
   *
   * @param identifier Unique Scope distinguisher
   */

  private Scope(
    String identifier) {

    this.identifier = identifier;
  }

  /**
   * Returns the identifier that this Scope represents
   *
   * @return String The identifier of the Scope
   */

  public String getIdentifier() {
    return(identifier);
  }

  /**
   * Compares Scope to the specified object.
   * The result is true, if and only if both Scope's identifiers are equal.
   *
   * @return true if Scopes are equal
   */

  public boolean equals(
    Object object) {

    if (object == this) return(true);

    if (object instanceof Scope) {
      Scope thatScope = (Scope) object;

      return(identifier.equals(thatScope.getIdentifier()));
    }

    return(false);
  }

  /**
   * Provide a hash code for this Scope.
   * The Scope hash code is equivalent to the Scope's identifier hash code.
   *
   * @return Hash code value for this Scope
   */

  public int hashCode() {
    return(identifier.hashCode());
  }

  /**
   * Provides a String representation of Scope.
   *
   * @return String representation of Scope
   */

  public String toString() {
    return("[" + identifier + "]");
  }
}
