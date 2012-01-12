/*
 * @(#)Space.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.space;

import java.io.Serializable;

/**
 * <p>
 * An "enumerated" type representing the spaces that a meems live in.
 * <p>
 * Note: spaces can overlap, or not, for example a type space will have meems in it
 * that also appear in other spaces, hyperspace also can contain meems that are
 * transient or persist in meem store. Transient meems on the other hand might not
 * appear in hyperspace at all, but only exist in the context of the things that created
 * them.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-04-22)
 * </p>
 * @author  Andy Gelme
 * @author  MG
 * @version 1.0
 * @see org.openmaji.meem.MeemPath
 */

public final class Space implements Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

  /**
   * HyperSpace for Meems that require simple grouping (Category) relationships
   */
  public static final Space HYPERSPACE = new Space("hyperSpace", false);

  /**
   * MeemStore for Meems that do require permanent storage
   */
  public static final Space MEEMSTORE = new Space("meemStore", true);

  /**
   * TransientSpace for Meems that don't require permanent storage.
   * Used for dynamically created, temporary Meems and during bootstrap phase.
   * Note: TransientSpace is a form of storage, just not a permanent one !
   */
  public static final Space TRANSIENT = new Space("transient", true);

  /**
   * TypeSpace for locating Meems based upon the Facets they provide.
   * Effectively allows Meems to be discovered by "interface type".
   */
  public static final Space TYPESPACE = new Space("typeSpace", false);

  /**
   * Uniquely distinguish one type of Space from another
   */
  private String type;

  /**
   * Indicate whether this Space can be used for Meem Definition and Content
   */
  private boolean storage;

  /**
   * Create Space.
   *
   * @param type    Uniquely distinguishes the type of Space
   * @param storage Indicates whether this type of Space provides "storage"
   * @exception IllegalArgumentException Space type must not be null
   */
  private Space(
    String  type,
    boolean storage) {

    if (type == null) {
      throw new IllegalArgumentException("Space type must not be 'null'");
    }

    this.type    = type;
    this.storage = storage;
  }

  /**
   * Provide the Space type.
   *
   * @return String Space type
   */
  public String getType() {
    return(type);
  }

  /**
   * Indicates whether this is a storage Space.
   *
   * @return True if this is a storage Space
   */
  public boolean isStorage() {
    return(storage);
  }

  /**
   * Compares Space to the specified object.
   * The result is true, only if both the type and storage are equal.
   *
   * @return true if Spaces are equal
   */
  public boolean equals(
    Object object) {

    if (object == this) return(true);

    if ((object instanceof Space) == false) return(false);

    Space thatSpace = (Space) object;

    if (storage != thatSpace.isStorage()) return(false);

    return (type.equals(thatSpace.getType()));
  }

  /**
   * Provides the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   *
   * @return Space hashCode
   */
  public int hashCode() {
    return(type.hashCode() ^ new Boolean(storage).hashCode());
  }

  /**
   * Provides a String representation of Space.
   *
   * @return String representation of Space
   */
  public String toString() {
    return(
      getClass().getName()   + "[" +
      "type="      + type    +
      ", storage=" + storage +
      "]"
    );
  }
}