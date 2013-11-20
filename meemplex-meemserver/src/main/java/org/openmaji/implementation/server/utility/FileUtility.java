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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtility
{
  private static final int BUFFER_SIZE = 16384;

  public static void copyFile(String source, String destination) throws IOException
  {
    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(source));
    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination));
    int bytes;
    byte[] buffer = new byte[BUFFER_SIZE];

    while ( ( bytes = inputStream.read(buffer) ) != -1 )
    {
      outputStream.write(buffer, 0, bytes);
    }
    outputStream.flush();
    outputStream.close();
    inputStream.close();
  }

  public static void dumpToFile(String name, String string) throws IOException
  {
    File file = new File(name);
    File parentFile = file.getParentFile();
    if ( parentFile.exists() == false )
    {
      if ( parentFile.mkdirs() == false )
        throw new IOException("Unable to create directories for "+parentFile.getAbsoluteFile());
    }
    FileWriter writer = new FileWriter(file);
    writer.write(string);
    writer.flush();
    writer.close();
  }

  public static void moveFile(String currentName, String newName) throws IOException
  {
    File file = new File(currentName);
    File newFile = new File(newName);
    if ( file.renameTo(newFile) == false )
      throw new IOException("Unable to move file to "+newFile.getParent());
  }

  public static void deleteAllFilesRecursively(String directoryName) throws IOException
  {
    File dir = new File(directoryName);
    if ( dir.exists() )
    {
      deleteAllFilesRecursively(dir);
      dir.delete();
    }
  }
  
  public static void deleteAllFilesRecursively(File directory) throws IOException
  {
    File[] files = directory.listFiles();
    for ( int i = 0; i < files.length; i++ )
    {
      File file = files[i];
      if ( file.exists() )
      {
        if ( file.isDirectory() )
        {
          deleteAllFilesRecursively(file);
        }
        if ( file.delete() == false )
        {
          throw new IOException("Unable to delete "+file.getCanonicalPath());
        }
      }
    }
  }
}
