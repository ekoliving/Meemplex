package org.openmaji.implementation.server.utility;

import java.util.ArrayList;

public class TraceFlagUtility
{
  private static ArrayList flags = new ArrayList();
  private static ArrayList descriptions = new ArrayList();
  private static int longestFlag = 0;

  public static void add(String flag, String description)
  {
    flags.add(flag);
    descriptions.add(description);
    if ( flag.length() > longestFlag ) longestFlag = flag.length();
  }

	public static String getTraceFlagDescriptions()
	{
    StringBuffer buffer = new StringBuffer();
    for ( int i=0; i<flags.size(); i++ )
    {
      String flag = (String) flags.get(i);
      String description = (String) descriptions.get(i);
      buffer.append(StringUtility.pad(flag,longestFlag+4));
      buffer.append(description);
      buffer.append('\n');
    }
    return buffer.toString();
	}
}
