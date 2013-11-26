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
 */

package org.openmaji.implementation.common;

import org.openmaji.common.Binary;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * <p>
 * The BinaryInput2Base provides the common implementation for all two
 * Binary input, single Binary output Meems.
 * </p>
 * <p>
 * The specific logic Meem, e.g. Binary?2Wedge, provides the hand-built
 * WedgeDefinition using the getWedgeDefinition() method and provides an
 * appropriate implementation of the logicOperation() method.
 * </p>
 * <p>
 * The wedges based on this class are designed to be assembled with a
 * BinaryWedge to produce a functioning Binary logic Meem.  The reason
 * for this approach is that a given wedge can only have a single Facet
 * (Java interface) of a specific type, e.g. Binary.  However, since two
 * Binary inputs are needed, two wedges are combined to obtain the desired
 * result.
 * </p>
 * <p>
 * The WedgeDefinition created by the default introspection approach will
 * produce two Facets with the same identifier "binary", which is invalid.
 * For this reason, a WedgeDefinition needs to be built by hand.
 * See "https://dev.majitek.com/snipsnap//space/maji-faq".
 * In time, this will be improved to be much less tedious.
 * </p>
 * <p>
 * The resulting Binary logic Meem is designed to be used with strong
 * Dependencies and contentRequired set true on the upstream Meem's
 * Binary output Facets.  This means that it can support downstream Meems
 * whose Binary input Facet is also a strong Dependency with contentRequired.
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
 * inputs, e.g. a strong or weak many Dependency.  Of course, if you can
 * find a good way to use multiple inputs, then "go for it" !
 * </p>

 * <p>
 * Note: Implementation thread safe = Requires ReentrantGuard (2004-01-28)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.common.Binary
 * @see org.openmaji.implementation.common.BinaryAND2Wedge
 * @see org.openmaji.implementation.common.BinaryOR2Wedge
 * @see org.openmaji.implementation.common.BinaryWedge
 * @see org.openmaji.implementation.common.BinaryXOR2Wedge
 */

public abstract class BinaryInput2Base implements Binary {

  /**
   * Binary output (outbound Facet)
   */

  public Binary binaryOutput;
  public final ContentProvider<Binary> binaryOutputProvider = new ContentProvider<Binary>() {
        /**
         * <p>
         * Send content to a Binary client that has just had it's Reference added.
         * </p>
         * @param target           Reference to the target Meem
         * @param filter           No Filters are currently implemented
         */
        public void sendContent(Binary target, Filter filter) {
          target.valueChanged(output);
        }
  };


  /**
   * Conduit for receiving the second Binary input
   */

  public Binary binaryControlConduit = new BinaryControlConduit();

  /**
   * Conduit for sending the Binary result of the logical operation
   */

  public Binary binaryStateConduit = null;

  /**
   * Transient boolean input value from the inbound Binary Facet
   */

  private boolean input1 = false;

  /**
   * Transient boolean input value from the Binary Control Conduit
   */

  private boolean input2 = false;

  /**
   * Transient Binary result of the logical operation
   */

  private boolean output = false;

  /**
   * Update the output by performing the logical operation on the two inputs.
   * Deliver the resulting output to both the "binaryOutput" outbound Binary
   * Facet and the Binary State Conduit.
   */

  private void outputChanged() {
    output = logicOperation(input1, input2);

    binaryOutput.valueChanged(output);
    binaryStateConduit.valueChanged(output);
  }

  /**
   * Perform the logical operation on the two inputs.
   *
   * @param input1 boolean input 1
   * @param input2 boolean input 2
   * @return boolean result of the logical operation
   */

  protected abstract boolean logicOperation(
    boolean input1,
    boolean input2);

/* ---------- Binary Facet method(s) --------------------------------------- */

  /**
   * Respond to a value change by recalculating and distributing the output.
   *
   * @param value Changed boolean value
   */

  public void valueChanged(
    boolean value) {

    input1 = value;
    outputChanged();
  }

/* ---------- BinaryStateConduit ------------------------------------------- */

  /**
   * This class handles incoming Binary control messages from other
   * Wedges in the Meem.
   */

  class BinaryControlConduit implements Binary {

    /**
     * Respond to a value change by recalculating and distributing the output.
     *
     * @param value Changed boolean value
     */

    public void valueChanged(
      boolean value) {

      input2 = value;
      outputChanged();
    }
  }


/* ---------- BinaryInput2 Meem WedgeDefinition ---------------------------- */

  /**
   * Hand built WedgeDefinition that uses a different inbound Facet identifier
   * from the default, so that conflicts with the BinaryWedge are avoided.
   *
   * @param wedgeImplementationClassName Determines the specific Wedge to use
   * @return WedgeDefinition with non-conflicting Facet identifiers
   */

  protected static WedgeDefinition getWedgeDefinition(
    String wedgeImplementationClassName) {

    // ----------------------------------
    // Inbound Binary Facet "binaryInput"

    FacetDefinition facetDefinition1 = new FacetDefinition(
      new FacetInboundAttribute(
        "binaryInput",
        "org.openmaji.common.Binary",
        true
      )
    );

    // ------------------------------------
    // Outbound Binary Facet "binaryOutput"

    FacetDefinition facetDefinition2 = new FacetDefinition(
      new FacetOutboundAttribute(
        "binaryOutput",
        "org.openmaji.common.Binary",
        "binaryOutput"
      )
    );

    // ------------------------------------------------------------
    // WedgeDefinition that includes the inbound and outbound Facet

    WedgeDefinition wedgeDefinition = new WedgeDefinition(
      new WedgeAttribute(wedgeImplementationClassName)
    );

    wedgeDefinition.addFacetDefinition(facetDefinition1);
    wedgeDefinition.addFacetDefinition(facetDefinition2);

    return(wedgeDefinition);
  }
}
