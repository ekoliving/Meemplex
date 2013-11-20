/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.common;

/**
 * <p>
 * Concrete implementation of Position used to represent an integer value.
 * The default increment/decrement amount is 1 but an alternative can be specified in the constructor.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not completed
 * </p>
 * @author  Christos Kakris
 * @version 1.0
 */

public class IntegerPosition implements Position
{
	private static final long serialVersionUID = 6424227717462161145L;
	
  protected int value;
  protected int increment = 1;
  protected int minimum = Integer.MIN_VALUE;
  protected int maximum = Integer.MAX_VALUE;

  /**
   * An instance of Position which stores its value with an internal type of int and has
   * an initial value of 0, has a default range of Integer.MIN_VALUE to Integer.MAX_VALUE, and a
   * default increment/decrement value of 1.
   */

  public IntegerPosition() { this.value = 0; }

  /**
   * An instance of Position which stores its value with an internal type of int and has
   * an initial as specified, has a default range of Integer.MIN_VALUE to Integer.MAX_VALUE, and a
   * default increment/decrement value of 1.
   * 
   * @param value The initial value of this Position.
   */

  public IntegerPosition(int value) { this.value = limitToRange(value); }

  /**
   * An instance of Position which stores its value with an internal type of int and has
   * an initial as specified, has a default range of Integer.MIN_VALUE to Integer.MAX_VALUE, and an
   * increment/decrement value as specified.
   * 
   * @param value The initial value of this Position.
   * @param increment The increment/decrement amount for this Position.
   */

  public IntegerPosition(int value, int increment)
  {
    this.value = limitToRange(value); this.increment = increment;
  }

  /**
   * An instance of Position which stores its value with an internal type of int and has
   * an initial value as specified, has the range as specified, and an
   * increment/decrement value as specified.
   * 
   * @param value The initial value of this Position.
   * @param increment The increment/decrement amount for this Position.
   * @param minimum The The minimum value this Position can have.
   * @param maximum The The maximum value this Position can have.
   */

  public IntegerPosition(int value, int increment, int minimum, int maximum)
  {
    this.minimum = minimum; this.maximum = maximum; this.increment = increment; this.value = limitToRange(value);
  }

  public void valueChanged(int value) { this.value = limitToRange(value); }
  public void valueChanged(long value) { this.value = limitToRange((int)value); }
  public void valueChanged(float value) { this.value = limitToRange((int)value); }
  public void valueChanged(double value) { this.value = limitToRange((int)value); }

  public void increment()
  {
    this.value = limitToRange(value+increment);
    round();
  }

  public void decrement()
  {
    this.value = limitToRange(value-increment);
    round();
  }

  public int getIncrementAsInt() { return increment; }
  public long getIncrementAsLong() { return (long) increment; }
  public float getIncrementAsFloat() { return (float) increment; }
  public double getIncrementAsDouble() { return (double) increment; }

  public void setToMinimum() { value = minimum; }
  public void setToMaximum() { value = maximum; }

  public int    intValue() { return value; }
  public long   longValue() { return (long) value; }
  public float  floatValue() { return (float) value; }
  public double doubleValue() { return (double) value; }
  
  public int getMinimumAsInt() { return minimum; }
  public long getMinimumAsLong() { return (long) minimum; }
  public float getMinimumAsFloat() { return (float) minimum; }
  public double getMinimumAsDouble() { return (double) minimum; }

  public int getMaximumAsInt() { return maximum; }
  public long getMaximumAsLong() { return (long) maximum; }
  public float getMaximumAsFloat() { return (float) maximum; }
  public double getMaximumAsDouble() { return (double) maximum; }

  private int limitToRange(int value)
  {
    if ( value < minimum ) return minimum;
    if ( value > maximum ) return maximum;
    return value;
  }

  private void round()
  {
    value = Math.round((float)value/(float)increment) * increment;
  }

  /*
      The next four methods provide a way for us to scale the value of a position to a
      different range. We do so using some simple algebra.

      This diagram represents our Position object where a=minimum b=value and c=maximum

               a     b               c
               x-----x---------------x

      The following represents the scaling to a different range we wish to apply to the value.
      Note that A=minimum and C=maximum of the range we are scaling to, and B = the new value.

               A          B                       C
               x----------x-----------------------x

      Simple algebra states that the ratios of ab:ac and AB:AC are equal. So:

                  ab     AB
                 ---- = ----
                  ac     AC

      And the length AB can be expressed:

                  AB = ( AC * ab ) / ac                 perform the multiplication before the division
                                                        so that we don't lose precision.

      Or, finally as:

                  AB = ( ( C - A ) * ( b - a ) ) / ( c - a )

      To determine the value of B we simply add the length AB to A.

      I haven't done anything particularly sophisticated here but it did take a few unit tests to
      confirm that I got the details right.  To read the code below just substitue the following:

          this.minimum -> a
          this.value   -> b
          this.maximum -> c
          minimum      -> A
          maximum      -> C
   */

  public int getValue(int minimum, int maximum)
  {
    int AB = ( ( maximum - minimum ) * ( this.value - this.minimum ) ) / ( this.maximum - this.minimum );
    return minimum + AB;
  }

  public long getValue(long minimum, long maximum)
  {
    long AB = ( ( maximum - minimum ) * ( this.value - this.minimum ) ) / ( this.maximum - this.minimum );
    return minimum + AB;
  }

  public float getValue(float minimum, float maximum)
  {
    float AB = ( ( maximum - minimum ) * ( this.value - this.minimum ) ) / ( this.maximum - this.minimum );
    return minimum + AB;
  }

  public double getValue(double minimum, double maximum)
  {
    double AB = ( ( maximum - minimum ) * ( this.value - this.minimum ) ) / ( this.maximum - this.minimum );
    return minimum + AB;
  }

  public boolean equals(Object object)
  {
  	if ( object == null ) return false;
    if ( object == this ) return true;
    if ( object.getClass().equals(this.getClass()) == false ) return false;
    IntegerPosition that = (IntegerPosition) object;
    return ( this.value == that.value );
  }

  public int hashCode()
  {
    return value ^ increment ^ minimum ^ maximum;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(getClass().getName());
    buffer.append("[value=");
    buffer.append(value);
    buffer.append(",increment=");
    buffer.append(increment);
    buffer.append(",minimum=");
    buffer.append(minimum);
    buffer.append(",maximum=");
    buffer.append(maximum);
    buffer.append("]");
    return buffer.toString();
  }

  public String toParseableString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(value);
    buffer.append(" ");
    buffer.append(increment);
    buffer.append(" ");
    buffer.append(minimum);
    buffer.append(" ");
    buffer.append(maximum);
    return buffer.toString();
  }
}
