/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider providing support for some simple Binary Filters, e.g only
 *   output "false to true" value changes transitions or vice-versa.
 *
 * - Consider generating an error, if the sendContent() target is not a Binary.
 *   Another approach is that the framework should pre-verify the target type.
 *
 * - Consider creating an optimized version, that only informs the Binary
 *   Meem clients, if the value really changed.  Is it possible to do this
 *   as a Filter (which would be much more flexible) ?
 *
 * - Consider also using the input of a Binary Conduit, so that another
 *   Binary Wedge can be included in the same Meem (as an optimization).
 *
 * - Consider also outputing the value on a Binary Conduit, so that another
 *   Binary Wedge can be included in the same Meem (as an optimization).
 */

package org.openmaji.implementation.common;

import org.openmaji.common.Binary;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * <p>
 * The BinaryNOTWedge is used to invert the value of boolean type information.
 * </p>
 * <p>
 * This Wedge is simply designed to be used with its Binary input as a strong
 * Dependency and contentRequired set true on the upstream Meem's Binary output
 * Facet.  This means that it can support downstream Meems whose Binary input
 * is also a strong Dependency with contentRequired.
 * </p>
 * <p>
 * Alternatively, the downstream Meems can be less stringent and use either a
 * weak Dependency or contentRequired set false, if that suits their design.
 * </p>
 * <p>
 * If this Wedge's Binary input only uses a weak Dependency then it will not
 * correctly pass on failure to it's downstream clients.
 * </p>
 * <p>
 * If this Wedge's Binary input has a Dependency with contentRequired set
 * false, then downstream Meems must not have contentRequired set true.
 * Otherwise they'll get an initial value of "false", if this Meem hasn't
 * already received an updated value from the upstream Meem.
 * </p>
 * <p>
 * The direction of the Dependencies should be in the opposite direction to
 * the information flow.  The Binary input should depend upon a single
 * upstream Meem.  And, the Binary output should be depended upon by one
 * or more downstream Meems.  Generally speaking, it doesn't make logical
 * sense to swap around the direction of the Dependencies for a logical NOT
 * operation.
 * </p>
 * <p>
 * This Wedge has not been designed to work sensibly with multiple Binary
 * inputs, e.g. a strong or weak many Dependency.  Usually, this doesn't
 * make sense for a logical NOT operation.  Of course, if you can find a
 * good way to use multiple inputs, then "go for it" !
 * </p>
 * <p>
 * Note: Implementation thread safe = ReentrantGuard (2004-01-28)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.common.Binary
 */

public class BinaryNOTWedge implements Binary, Wedge {

  /**
   * Binary client (out-bound Facet)
   */

  public Binary binaryClient;
    public final ContentProvider<Binary> binaryClientProvider = new ContentProvider<Binary>() {
          /**
           * <p>
           * Send content to a Binary client that has just had it's Reference added.
           * </p>
           * @param target           Reference to the target Meem
           * @param filter           No Filters are currently implemented
           */
          public void sendContent(Binary target, Filter filter) {
            target.valueChanged(value);
          }
    };


  /**
   * Transient boolean state maintained by this Wedge
   */

  private boolean value = false;

/* ---------- Binary Facet method(s) --------------------------------------- */

  /**
   * Respond to a value change by passing the logical NOT of the value
   * on to any Meems depending upon the outbound Binary Facet.
   *
   * @param value Changed boolean value
   */

  public void valueChanged(boolean value) {

	  //if (this.value == value) {
		  this.value = ! value;
		  binaryClient.valueChanged(this.value);
	  //}
  }
}
