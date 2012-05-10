/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.common;

/**
 * <p>
 * A NumberValue is a concrete implementation of the Value interface whose
 * base type is a Number (one of BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short).
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 */

public class NumberValue implements Value
{
	private static final long serialVersionUID = 6424227717462161145L;
	
  private Number value = null;

  /**
   * Constructs a new instance of NumberValue using the Number passed as a paramater
   * to the constructor. Note that if you pass a NULL then the underling value will
   * be set to Integer(0).
   * 
   * @param value The underlying Number for this NumberValue
   */

  public NumberValue(Number value)
  {
    this.value = ( value == null ? new Integer(0) : value );
  }

  /**
   * Returns the base type as an int.
   * 
   * @return The integer value of the underlying Number
   */

  public int integerValue() { return value.intValue(); }

  /**
   * Returns true if the underlying Number is greater than zero
   * otherwise it returns a false;
   * 
   * @return The boolean value of the underlying Number
   */

  public boolean booleanValue() { return ( integerValue() > 0 ); }

  /**
   * Returns the base type as a float.
   * 
   * @return The float value of the underlying Number
   */

  public float floatValue() { return value.floatValue(); }

  public String toString() { return value.toString(); }
}

