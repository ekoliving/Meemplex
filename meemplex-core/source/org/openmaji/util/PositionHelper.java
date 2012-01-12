/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmaji.common.FloatPosition;


public class PositionHelper
{
  /**
   * Parses a String containing a series of four floating point numbers
   * and returns a FloatPosition.
   * 
   * @param positionString String containing four floating point numbers
   * @return An instance of FloatPosition
   * @throws IllegalArgumentException If the string could not be parsed
   */
  public static FloatPosition ParseFloatPosition(String positionString) throws IllegalArgumentException
  {
    StringBuffer buffer = new StringBuffer();
    for ( int i=0; i < 4; i++ )
    {
      buffer.append(RegexConst.WHITE_SPACE);
      buffer.append(RegexConst.FLOAT_REGEX);
    }
    buffer.append(RegexConst.WHITE_SPACE);

    Pattern pattern = Pattern.compile(buffer.toString());

    Matcher matcher = pattern.matcher(positionString);
    if ( ! matcher.matches() )
    {
      throw new IllegalArgumentException("Bad format specified for position");
    }

    float value = Float.parseFloat(matcher.group(1));
    float increment = Float.parseFloat(matcher.group(3));
    float min = Float.parseFloat(matcher.group(5));
    float max = Float.parseFloat(matcher.group(7));
    return new FloatPosition(value,increment,min,max);
  }
}
