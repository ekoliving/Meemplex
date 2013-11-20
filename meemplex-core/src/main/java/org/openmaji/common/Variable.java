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
 * This Facet is implemented by wedges that can store a Value.
 * For maximum flexibility we will allow that Value to be anything,
 * well almost anything.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 * @see org.openmaji.common.Value
 */

public interface Variable extends Facet
{
  /**
   * Change the value of this Variable.
   * 
   * @param value   The new value for this Variable.
   */

  public void valueChanged(Value value);
}

