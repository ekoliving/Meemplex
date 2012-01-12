/*
 * @(#)FacetInformation.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.meem;

import org.openmaji.system.meem.FacetItem;

import net.jini.core.entry.Entry;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class FacetInformation implements Entry {
	private static final long serialVersionUID = 1348949329874643987L;

  public FacetItem facetItem = null;

  public FacetInformation() {
  }

  public FacetInformation(
    FacetItem facetItem) {

    this.facetItem = facetItem;
  }

  public synchronized boolean equals(
    Object object) {

    if (object == this) return(true);

    if ((object instanceof FacetInformation) == false) return(false);

    FacetInformation thatFacetItemEntry = (FacetInformation) object;

    if (facetItem == null) {
      if (thatFacetItemEntry.facetItem != null) return(false);
    }
    else {
      if (facetItem.equals(thatFacetItemEntry.facetItem) == false) {
        return(false);
      }
    }

    return(true);
  }

  public synchronized int hashCode() {
    return(facetItem == null  ?  0  :  facetItem.hashCode());
  }

  public String toString() {
    return(getClass().getName() + "[" + facetItem + "]");
  }
}
