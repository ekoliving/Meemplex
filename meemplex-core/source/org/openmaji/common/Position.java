/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.common;

import java.io.Serializable;

/**
 * <p>
 * Represents the state of a Linear by encapsulating the value of its position, the type of
 * the value, and the allowed range for the value.
 * </p>
 * 
 * <p>
 * A concrete implementation of
 * this interface may restrict the value to within a specific range. A concrete
 * implementation may also provide a 'natural' increment and decrement amount that
 * differs from the underlying resolution of the type. For example an implementation
 * that has underlying value type of integer may provide a convenience to increment/decrement
 * by 10 even though it has a resolution down to unit values.
 * </p>
 * 
 * <p>
 * Apart from the usual getter and setter methods there are also convience methods which
 * are defined allowing the value of the Position to be scaled to a different range. 
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Christos Kakris
 * @version 1.0
 */

public interface Position extends Serializable
{
  /**
   * Set this Position to a new value.
   * 
   * @param value The new value specified as an int but stored internally using
   *              the base type of the concrete implementation.
   */

  public void valueChanged(int value);

  /**
   * Set this Position to a new value.
   * 
   * @param value The new Value specified as a long but stored internally using
   *              the base type of the concrete implementation.
   */

  public void valueChanged(long value);

  /**
   * Set this Position to a new value.
   * 
   * @param value The new Value specified as a float but stored internally using
   *              the base type of the concrete implementation.
   */
  
  public void valueChanged(float value);

  /**
   * Set this Position to a new value.
   * 
   * @param value The new Value specified as a double but stored internally using
   *              the base type of the concrete implementation.
   */
  
  public void valueChanged(double value);

  /**
   * Increment the value of this Position a single step using either the natural
   * increment amount for this concrete implementation or by the amount specified
   * in the constructor.
   */

  public void increment();

  /**
   * Decrement the value of this Position a single step using either the natural
   * decrement amount for this concrete implementation or by the amount specified
   * in the constructor.
   */

  public void decrement();

  public int getIncrementAsInt();
  public long getIncrementAsLong();
  public float getIncrementAsFloat();
  public double getIncrementAsDouble();

  /**
   * Set the value of this Position to its minimum possible range. This minimum value will
   * either be the default minimum allowed by the underlying implementing class
   * or the minimum specified in the constructor.
   */

  public void setToMinimum();

  /**
   * Set the value of this Position to its maximum possible range. This maximum value will
   * either be the default maximum allowed by the underlying implementing class
   * or the maximum specified in the constructor.
   */

  public void setToMaximum();

  public int getMinimumAsInt();
  public long getMinimumAsLong();
  public float getMinimumAsFloat();
  public double getMinimumAsDouble();

  public int getMaximumAsInt();
  public long getMaximumAsLong();
  public float getMaximumAsFloat();
  public double getMaximumAsDouble();

  /**
   * The value of this Position as an int.
   * 
   * @return The value of this Position as an int.
   */

  public int intValue();

  /**
   * The value of this Position as a long.
   * 
   * @return The value of this Position as a long.
   */

  public long longValue();

  /**
   * The value of this Position as a float.
   * 
   * @return The value of this Position as a float.
   */

  public float floatValue();

  /**
   * The value of this Position as a double.
   * 
   * @return The value of this Position as a double.
   */

  public double doubleValue();

  /**
   * The value of this Position scaled to another range. This returns the value of
   * the position as an int but scaled to correctly fit within the range as specified
   * by the int parameters.
   * 
   * @param minimum The minimum value of the scaling range.
   * @param maximum The maximum value of the scaling range.
   * @return The scaled value as an int.
   */

  public int getValue(int minimum, int maximum);

  /**
   * The value of this Position scaled to another range. This returns the value of
   * the position as a long but scaled to correctly fit within the range as specified
   * by the long parameters.
   * 
   * @param minimum The minimum value of the scaling range.
   * @param maximum The maximum value of the scaling range.
   * @return The scaled value as a long.
   */

  public long getValue(long minimum, long maximum);

  /**
   * The value of this Position scaled to another range. This returns the value of
   * the position as a float but scaled to correctly fit within the range as specified
   * by the float parameters.
   * 
   * @param minimum The minimum value of the scaling range.
   * @param maximum The maximum value of the scaling range.
   * @return The scaled value as a float.
   */

  public float getValue(float minimum, float maximum);

  /**
   * The value of this Position scaled to another range. This returns the value of
   * the position as a double but scaled to correctly fit within the range as specified
   * by the double parameters.
   * 
   * @param minimum The minimum value of the scaling range.
   * @param maximum The maximum value of the scaling range.
   * @return The scaled value as a double.
   */

  public double getValue(double minimum, double maximum);

  public String toParseableString();
}
