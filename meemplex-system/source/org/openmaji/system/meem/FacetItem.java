/*
 * @(#)FacetItem.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.meem;

import java.io.Serializable;

import org.openmaji.meem.definition.Direction;


/**
 * <p>
 * An object that contains general descriptive information about a facet.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class FacetItem implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

  public String identifier = null;

  public String interfaceName = null;

  public Direction direction = null;

  public FacetItem() {
  }

/**
 * Constructor for a particular facet.
 * 
 * @param identifier the facet identifier.
 * @param interfaceName the name of the interface it implements.
 * @param direction the direction of the facet.
 */
  public FacetItem(
    String    identifier,
    String    interfaceName,
    Direction direction) {

    this.identifier    = identifier;
    this.interfaceName = interfaceName;
    this.direction     = direction;
  }

  public synchronized boolean equals(
    Object object) {

    if (object == this) return(true);

    if ((object instanceof FacetItem) == false) return(false);

    FacetItem thatFacetItem = (FacetItem) object;

    if (identifier == null) {
      if (thatFacetItem.identifier != null) return(false);
    }
    else {
      if (identifier.equals(thatFacetItem.identifier) == false) return(false);
    }

    if (interfaceName == null) {
      if (thatFacetItem.interfaceName != null) return(false);
    }
    else {
      if (interfaceName.equals(thatFacetItem.interfaceName) == false) {
        return(false);
      }
    }

    if (direction == null) {
      if (thatFacetItem.direction != null) return(false);
    }
    else {
      if (direction.equals(thatFacetItem.direction) == false) return(false);
    }

    return(true);
  }

  public synchronized int hashCode() {
    int hashCode = 0;

    if (identifier != null) hashCode += identifier.hashCode();

    if (interfaceName != null) hashCode += interfaceName.hashCode();

    if (direction != null) hashCode += direction.hashCode();

    return(hashCode);
  }


  public String toString() {
    return(
      getClass().getName() + "[" +
        "identifier="    + identifier      +
      ", interfaceName=" + interfaceName +
      ", direction="     + direction +
      "]"
    );
  }
}
