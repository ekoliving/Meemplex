/*
 * @(#)MeemPathImpl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Implement "new MeemPathImpl(String)".
 */

package org.openmaji.implementation.server.meem;

import java.io.Serializable;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;


/**
 * <p>
 * MeemPathImpl describes a location that can be resolved into a Meem.
 * </p>
 * <p>
 * A MeemPathImpl consists of a reference to a Space and to a location
 * within that Space, which identifies a specific Meem.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-03-19)
 * </p>
 * @author  Andy Gelme
 * @author  MG
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.MeemPath
 */

public class MeemPathImpl implements MeemPath, Serializable {
	private static final long serialVersionUID = 2644575427909499639L;

	public MeemPathImpl(Space space, String location) {
		// TODO[peter] Don't allow either parameter to be null
		this.space = space;
		this.location = location;
	}
	
  /**
   * Type of Space that may contain the Meem
   */

  private final Space space;

  /**
   * Location within the Space that identifies the specific Meem
   */

  private final String location;

  /**
   * Provide the location within the Space that identifies the specific Meem.
   *
   * @return Location within the Space that identifies the specific Meem
   */

  public String getLocation() {
    return(location);
  }

  /**
   * Provide the type of Space that may contain the Meem.
   *
   * @return Type of Space that may contain the Meem
   */

  public Space getSpace() {
    return(space);
  }

  /**
   * Indicates whether the MeemPathImpl refers to a storage Space.
   *
   * @return True if the MeemPathImpl refers to a storage Space
   */

  public boolean isDefinitive() {
    return(space.isStorage());
  }

  /**
   * Compares MeemPathImpl to the specified object.
   * The result is true, only if both the spaces and locations are equal.
   *
   * @return true if MeemPathImpls are equal
   */

  public boolean equals(
    Object object) {

    if (object == this) return(true);

    if ((object instanceof MeemPathImpl) == false) return(false);

    MeemPathImpl meemPath = (MeemPathImpl) object;

    if (space != null) {
      if (space.equals(meemPath.getSpace()) == false) return(false);
    }

    if (location != null) {
      if (location.equals(meemPath.getLocation()) == false) return(false);
    }

    return(true);
  }

  /**
   * Provides the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   *
   * @return MeemPathImpl hashCode
   */

  public int hashCode() {
    int hashCode = 0;

    if (space != null) hashCode ^= space.hashCode();

    if (location != null) hashCode ^= location.hashCode();

    return(hashCode);
  }

  /**
   * Provides a String representation of MeemPathImpl.
   *
   * @return String representation of MeemPathImpl
   */

  public String toString() {
  	String type;
  	if (space == null) {
  		type = "";
  	}
  	else {
  		type = space.getType();
  	}
    return(type + ":" + location);
  }
}
