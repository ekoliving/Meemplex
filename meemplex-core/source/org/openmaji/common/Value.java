/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.common;

import java.io.Serializable;

/**
 * <p>
 * The Value interface is implemented by classes that are used to represent
 * a generic value. This type is used by the Variable Facet. Value is designed
 * to be reasonably malleable so that values of different
 * types can be easily assigned to the same Variable.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 * @see org.openmaji.common.Variable
 */

public interface Value extends Serializable
{
  /**
   * The value as an int.
   * 
   * @return   The value as an int.
   */

  public int integerValue();

  /**
   * The value as a boolean.
   * 
   * @return   The value as a boolean.
   */

  public boolean booleanValue();

  /**
   * The value as a float.
   * 
   * @return   The value as a float.
   */

  public float floatValue();

  /**
   * The value as a String.
   * 
   * @return   The value as a String.
   */

  public String toString();
}

