/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class URLUtility
{
  private static final int BUFFER_SIZE = 2^14;

  /**
   * Utility to download a remote file from a URL and place the
   * contents into a directory. A URL takes the form
   * <code>protocol://host/full/path/to/file?arguments</code>. This method
   * will create a file in the specified directory with a name generated from
   * the last portion of the path. For example if you specified a URL of
   * <code>http://www.server.com/documents/new/test.html</code> the filename
   * used will be <code>test.html</code>
   * 
   * @param url  The URL of the remote file
   * @param directoryName The name of the directory into which to place the downloaded file
   * @throws IOException When an error occurs either reading the remote file or writing it locally
   */
  public static String downloadToDirectory(URL url, String directoryName) throws IOException
  {
    BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
    String pathname = url.getPath();
    String filename = pathname;
    int index = pathname.lastIndexOf("/");
    if ( index != -1 )
    {
      filename = pathname.substring(index+1);
    }

    int bytes;
    byte[] buffer = new byte[BUFFER_SIZE];

    String destinationFilename = directoryName + File.separator + filename;
    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFilename));
    while ( ( bytes = inputStream.read(buffer) ) != -1 )
    {
      outputStream.write(buffer, 0, bytes);
    }
    outputStream.close();
    inputStream.close();
    
    return destinationFilename;
  }

}
