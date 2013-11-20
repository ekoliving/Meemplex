/*
 * Copyright 2006 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.util;

/**
 * This class contains a number of convenient Java regular expressions that
 * can be used for validating user input.
 *
 * @author Chris Kakris
 */

public class RegexConst
{
  public static final String WHITE_SPACE = "\\s*";

  /**
   * <p>A regular expression that can be used to parse a floating point
   * number complete with exponent specification. An example use of this
   * expression follows:</p>
   * <pre>
   *    String str = FLOAT_REGEX + WHITE_SPACE + FLOAT_REGEX;
   *    Pattern pattern = Pattern.compile(str);
   *    Matcher matcher = pattern.matcher("-1.2 3.4e5");
   *    if ( matcher.matches() )
   *    {
   *      float first = Float.parseFloat(matcher.group(1));
   *      float second = Float.parseFloat(matcher.group(3));
   *    }
   * </pre>
   */

  public static final String FLOAT_REGEX = "([-+]?[0-9]*[\\.]?[0-9]*([eE][-+]?[0-9]+)?)";

  public static final String IP_ADDRESS = "(\\b((25[0-5]|2[0-4]\\d|[01]\\d\\d|\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|[01]\\d\\d|\\d?\\d)\\b)";
  
}
