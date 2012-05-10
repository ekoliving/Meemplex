/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.common;

/**
 * <p>
 * A StringValue is a concrete implementation of the Value interface whose
 * base type is a String.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 */

public class StringValue implements Value
{
	private static final long serialVersionUID = 6424227717462161145L;

  private String value = null;

  /**
   * Constructs a new instance of StringValue using the String passed as a paramater
   * to the constructor. Note that if you pass a NULL then the underling value will
   * be set to an empty (zero length) string.
   * 
   * @param value The underlying Number for this NumberValue
   */

  public StringValue(String value)
  {
    this.value = ( value == null ? "" : value );
  }

  /**
   * Returns the base type as an int. Note that if the underlying String can't
   * be parsed as an integer then the value of 0 is returned.
   * 
   * @return The integer value of the underlying String
   */

  public int integerValue()
  {
    int intValue = 0;
    try
    {
      intValue = Integer.parseInt(value);
    }
    catch ( NumberFormatException ex ) {}
    return intValue;
  }

  /**
   * Returns true if the underlying string contains a positive integer
   * otherwise it returns a false;
   * 
   * @return The boolean value of the underlying String
   */

  public boolean booleanValue() { return ( integerValue() > 0 ); }

  /**
   * Returns the base type as a float. Note that if the underlying String
   * can't be parsed as a float then the value of 0.0 is returned.
   * 
   * @return The float value of the underlying String
   */

  public float floatValue()
  {
    float floatValue = 0.0f;
    try
    {
      floatValue = Float.parseFloat(value);
    }
    catch ( NumberFormatException ex ) {}
    return floatValue;
  }

  public String toString() { return value; }
}

