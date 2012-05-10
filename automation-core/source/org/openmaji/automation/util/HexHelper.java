/*
 * Copyright 2006 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.util;

public class HexHelper
{
  public static void toHexString(StringBuffer buffer, byte[] bytes, int numBytes)
  {
    buffer.append('[');
    if ( bytes != null )
    {
      for ( int i = 0; i < numBytes; i++ )
      {
        String chars = Integer.toHexString(bytes[i] & 0xFF);
        if ( chars.length() == 1 )
        { 
          buffer.append('0');
        }
        buffer.append(chars);
        if ( i < bytes.length - 1 )
        {
          buffer.append(' ');
        }
      }
    }
    else
    {
      buffer.append(" null ");
    }

    buffer.append(']');
  }

  public static void toHexString(StringBuffer buffer, char[] chars, int numChars)
  {
    buffer.append('[');
    if ( chars != null )
    {
      for ( int i = 0; i < numChars; i++ )
      {
        String temp = Integer.toHexString(chars[i]); // & 0xFF);
        switch ( temp.length() )
        {
          case 1: buffer.append("000"); break;
          case 2: buffer.append("00"); break;
          case 3: buffer.append('0'); break;
        }
        buffer.append(temp);
        if ( i < chars.length - 1 )
        {
          buffer.append(' ');
        }
      }
    }
    else
    {
      buffer.append(" null ");
    }

    buffer.append(']');
  }

  public static StringBuffer toHexString(byte[] bytes, int numBytes)
  {
    StringBuffer buffer = new StringBuffer();
    toHexString(buffer,bytes,numBytes);
    return buffer;
  }

  public static StringBuffer toHexString(char[] chars, int numChars)
  {
    StringBuffer buffer = new StringBuffer();
    toHexString(buffer,chars,numChars);
    return buffer;
  }

  public static StringBuffer toHexString(byte[] bytes)
  {
    return toHexString(bytes,bytes.length);
  }
  
  public static StringBuffer toHexString(char[] chars)
  {
    return toHexString(chars,chars.length);
  }
  
  public static StringBuffer toHexString(String string)
  {
    char[] chars = string.toCharArray();
    return toHexString(chars);
  }
  
  public static void main(String[] args) {
    System.out.println(toHexString(new byte[] { 'h', 'e', 'l', 'l', 'o' }));
    System.out.println(toHexString("hello\u1234"));
    System.out.println(toHexString(""));
  }
}
