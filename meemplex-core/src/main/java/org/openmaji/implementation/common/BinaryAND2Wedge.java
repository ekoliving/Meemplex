/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.common;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.WedgeDefinition;

/**
 * <p>
 * The BinaryAND2Wedge is used to "logically AND" the values of exactly two
 * Binary inputs.  One Binary input value comes from the "binary" inbound
 * Binary Facet.  The other Binary input value comes from the Binary Control
 * Conduit.  The resulting output is provided via both the "binaryOutput"
 * outbound Binary Facet and the Binary State Conduit.
 * </p>
 * <p>
 * This Wedge extends the BinaryInput2Base class, which provides the common
 * implementation for all two Binary input, single Binary output Meems.
 * </p>
 * <p>
 * Note: Implementation thread safe = Requires ReentrantGuard (2004-01-28)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.implementation.common.BinaryInput2Base
 */

public class BinaryAND2Wedge extends BinaryInput2Base implements Wedge {

  /**
   * WedgeDefinition with altered inbound Facet identifiers to avoid conflict
   */

  public static final WedgeDefinition WEDGE_DEFINITION =
    BinaryInput2Base.getWedgeDefinition(
      "org.openmaji.implementation.common.BinaryAND2Wedge"
    );

  /**
   * Perform the logical AND operation on the two inputs.
   *
   * @param input1 boolean input 1
   * @param input2 boolean input 2
   * @return boolean result of the logical AND operation
   */

  protected boolean logicOperation(
    boolean input1,
    boolean input2) {

    return(input1 & input2);
  }
}
