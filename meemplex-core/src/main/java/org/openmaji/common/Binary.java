/*
 * @(#)Binary.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.common;

import org.openmaji.meem.Facet;

/**
 * <p>
 * The Binary Facet is used to represent a change in the value of boolean
 * type information.  For example, a switch that may be turned on or off.
 * </p>
 * <p>
 * This Facet may be used symmetrically as both the out-bound and in-bound
 * interfaces for "control information", i.e. attempting to effect a change
 * in the state of some other Meem.  As well as being both the out-bound and
 * in-bound interfaces for "state information", i.e. informing other Meems
 * about a change in one's own state.  From a Maji framework perspective,
 * these situations are equivalent, even though from the perspective of an
 * application developer, there may be a conceptual difference.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @author  Christos Kakris
 * @version 1.0
 * @see org.openmaji.meem.Facet
 */

public interface Binary extends Facet {

	/**
	 * Default in-bound Binary Facet identifier
	 */
	public final static String DEFAULT_INBOUND_FACET_IDENTIFER = "binaryInput";

	/**
	 * Default out-bound Binary Facet identifier
	 */
	public final static String DEFAULT_OUTBOUND_FACET_IDENTIFIER = "binaryOutput";
	
  /**
   * <p>
   * Indicate that a value change should or has occurred.
   * </p>
   * <p>
   * For an out-bound Binary Facet, this is a request for any target Meems
   * to respond to a change in the boolean value.
   * </p>
   * <p>
   * For an in-bound Binary Facet, this indicates that a source Meem is
   * effecting a change in boolean value for this Meem.
   * </p>
   * @param value Changed boolean value
   */

  public void valueChanged(boolean value);

}
