package org.openmaji.implementation.server.utility;

public class StringUtility
{
  /**
   * Pad a string with trailing spaces. If the length of the padded text
   * is less than the original text then we return a substring of that original text.
   * 
   * @param text The text you want padded
   * @param totalLength The length of the field
   * @return A new string instance that is padded to the specified length
   */

  public static String pad(String text, int totalLength)
  {
    if ( totalLength == text.length() ) return text;
    if ( totalLength < text.length() ) return text.substring(0,totalLength);
    int padChars = totalLength - text.length();
    StringBuffer padding = new StringBuffer(text);
    for ( int i=0; i<padChars; i++ ) padding.append(' ');
    return padding.toString();
  }
}
