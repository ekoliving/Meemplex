/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.common;

/**
 * <p>
 * Concrete implementation of Position used to represent a float value.
 * The default increment/decrement amount is 1.0f but an alternative can be specified in the constructor.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not completed
 * </p>
 * @author  Christos Kakris
 * @version 1.0
 */

public class FloatPosition implements Position
{
	private static final long serialVersionUID = 6424227717462161145L;
	
  protected float value;
  protected float increment = 1.0f;
  protected float minimum = Float.MIN_VALUE;
  protected float maximum = Float.MAX_VALUE;

  /**
   * An instance of Position which stores its value with an internal type of float and has
   * an initial value of 0.0f, has a default range of Float.MIN_VALUE to Float.MAX_VALUE, and a
   * default increment/decrement value of 1.0f.
   */

  public FloatPosition() { this.value = 0.0f; }

  /**
   * An instance of Position which stores its value with an internal type of float and has
   * an initial value as specified, has a default range of Float.MIN_VALUE to Float.MAX_VALUE, and a
   * default increment/decrement value of 1.0f.
   * 
   * @param value The initial value of this Position.
   */

  public FloatPosition(float value) { this.value = limitToRange(value); }
  
  /**
   * An instance of Position which stores its value with an internal type of float and has
   * an initial value as specified, has a default range of Float.MIN_VALUE to Float.MAX_VALUE, and an
   * increment/decrement value as specified.
   * 
   * @param value The initial value of this Position.
   * @param increment The increment/decrement amount for this Position.
   */

  public FloatPosition(float value, float increment)
  {
    this.value = limitToRange(value); this.increment = increment;
  }

  /**
   * An instance of Position which stores its value with an internal type of float and has
   * an initial value as specified, has the range as specified, and an
   * increment/decrement value as specified.
   * 
   * @param value The initial value of this Position.
   * @param increment The increment/decrement amount for this Position.
   * @param minimum The The minimum value this Position can have.
   * @param maximum The The maximum value this Position can have.
   */

  public FloatPosition(float value, float increment, float minimum, float maximum)
  {
    this.minimum = minimum; this.maximum = maximum; this.increment = increment; this.value = limitToRange(value);
  }

  public void valueChanged(int value) { this.value = limitToRange((float)value); }
  public void valueChanged(long value) { this.value = limitToRange((float)value); }
  public void valueChanged(float value) { this.value = limitToRange(value); }
  public void valueChanged(double value) { this.value = limitToRange((float)value); }

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

  public int getIncrementAsInt() { return (int) increment; }
  public long getIncrementAsLong() { return (long) increment; }
  public float getIncrementAsFloat() { return increment; }
  public double getIncrementAsDouble() { return (double) increment; }

  public void setToMinimum() { value = minimum; }
  public void setToMaximum() { value = maximum; }

  public int    intValue() { return (int) value; }
  public long   longValue() { return (long) value; }
  public float  floatValue() { return value; }
  public double doubleValue() { return (double) value; }

  public int getMinimumAsInt() { return (int) minimum; }
  public long getMinimumAsLong() { return (long) minimum; }
  public float getMinimumAsFloat() { return minimum; }
  public double getMinimumAsDouble() { return (double) minimum; }

  public int getMaximumAsInt() { return (int) maximum; }
  public long getMaximumAsLong() { return (long) maximum; }
  public float getMaximumAsFloat() { return maximum; }
  public double getMaximumAsDouble() { return (double) maximum; }

  private float limitToRange(float value)
  {
    if ( value < minimum ) return minimum;
    if ( value > maximum ) return maximum;
    return value;
  }

  private void round()
  {
    value = Math.round(value/increment) * increment;
  }

  /*
      For a description of the algorithm used by these getXX() scaling methods please refer
      to the source code for IntegerPosition (no point duplicating all of that here).
   */

  public int getValue(int minimum, int maximum)
  {
    return (int) getValue((float)minimum,(float)maximum);
  }

  public long getValue(long minimum, long maximum)
  {
    return (long) getValue((float)minimum,(float)maximum);
  }

  public float getValue(float minimum, float maximum)
  {
    float tmp = ( ( maximum - minimum ) * ( this.value - this.minimum ) ) / ( this.maximum - this.minimum );
    return minimum + tmp;
  }

  public double getValue(double minimum, double maximum)
  {
    return (double) getValue((float)minimum,(float)maximum);
  }

  public boolean equals(Object object)
  {
  	return 
    	( object == this ) ||
		( 	( object != null ) && 
			( object.getClass().equals(this.getClass()) ) && 
			( this.value == ((FloatPosition) object).value )
		);

  }

  public int hashCode()
  {
    return Float.floatToIntBits(value) ^ 
           Float.floatToIntBits(increment) ^
           Float.floatToIntBits(minimum) ^
           Float.floatToIntBits(maximum);
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
