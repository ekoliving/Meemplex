/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.common;

import org.openmaji.meem.Facet;

/**
 * <p>
 * In contrast to the Binary Facet which represents a single bit, or a
 * boolean value, the Linear Facet represents a value that falls within
 * a bounded linearly increasing range.
 * For example, a light that can be dimmed.
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

public interface Linear extends Facet
{
	/**
	 * Default in-bound Linear Facet identifier
	 */
	public final static String DEFAULT_INBOUND_FACET_IDENTIFER = "linearInput";

	/**
	 * Default out-bound Linear Facet identifier
	 */
	public final static String DEFAULT_OUTBOUND_FACET_IDENTIFIER = "linearOutput";
	
  /**
   * <p>
   * Indicate that a position change should or has occurred.
   * </p>
   * <p>
   * For an out-bound Linear Facet, this is a request for any target Meems
   * to respond to a change.
   * </p>
   * <p>
   * For an in-bound Linear Facet, this indicates that a source Meem is
   * effecting a change.
   * </p>
   * @param position Changed position
   */

  public void valueChanged(Position position);

}
