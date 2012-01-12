/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.common;

/**
 * <p>
 * Concrete implementation of a position used to represent a percentage value.
 * The allowed range is 0.0 to 100.0, inclusive, and the native type of the value is a float.
 * An attempt to set a value outside this range will cause the value to be set
 * to the appropriate limit of the range. The default increment/decrement amount is 10.0f but
 * an alternative can be specified in the constructor.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not completed
 * </p>
 * @author  Christos Kakris
 * @version 1.0
 */

public class PercentagePosition extends FloatPosition
{
	private static final long serialVersionUID = 6424227717462161145L;

  /**
   * An instance of Position which stores its value with an internal type of float and has
   * an initial value of 0.0f, has a range of 0.0f to 100.0f, and a
   * default increment/decrement value of 10.0f.
   */

  public PercentagePosition() { super(0.0f,10.0f,0.0f,100.0f); }

  /**
   * An instance of Position which stores its value with an internal type of float and has
   * an initial value as specified, has a range of 0.0f to 100.0f, and a
   * default increment/decrement value of 10.0f.
   * 
   * @param value The initial value of this Position.
   */

  public PercentagePosition(float value) { super(value,10.0f,0.0f,100.0f); }
  
  /**
   * An instance of Position which stores its value with an internal type of float and has
   * an initial value as specified, has a range of 0.0f to 100.0f, and an
   * increment/decrement value as specified.
   * 
   * @param value The initial value of this Position.
   * @param increment The increment/decrement amount for this Position.
   */

  public PercentagePosition(float value, float increment) { super(value,increment,0.0f,100.0f); }

  public boolean equals(Object object)
  {
  	if ( object == null ) return false;
    if ( object == this ) return true;
    if ( object.getClass().equals(this.getClass()) == false ) return false;
    if ( super.equals(object) == false ) return false;
    PercentagePosition that = (PercentagePosition) object;
    return ( this.value == that.value );
  }

  public int hashCode()
  {
    return super.hashCode();
  }
}
