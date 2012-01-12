/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.server.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to search for meemkit descriptor files.
 * It searches recursively both from an installed InterMajik and
 * also from a developer's checked-out copies of the meemkit projects.
 *
 * @author Chris Kakris
 */

public class MeemkitDescriptorLocator
{
	private static final Logger logger = Logger.getAnonymousLogger();
	
  private String startingSearchDirname;
  private ArrayList<String> additionalPaths = null;

	/**
	 * Create an instance with a starting location in the filesystem.
	 * 
	 * @param startingSearchDirname The starting location
	 */
	public MeemkitDescriptorLocator(String startingSearchDirname)
  {
    this.startingSearchDirname = startingSearchDirname;
  }
  
  /**
   * Create an instance with a starting location in the filesystem.
   * 
   * @param startingSearchDirname The starting location
   */
  public MeemkitDescriptorLocator(String startingSearchDirname, ArrayList<String> additionalPaths)
  {
    this.startingSearchDirname = startingSearchDirname;
    this.additionalPaths = additionalPaths;
  }
  
  /**
	 * Recursively search for all meemkit descriptor files in an installed
   * copy of InterMajik. The starting directory is assumed to include all
   * of the meemkit descriptors located closely together.
	 * 
	 * @return An array of Strings of the descriptor filenames
	 */
	public String[] locateAll()
  {
    ArrayList<String> list = new ArrayList<String>();
    
    list = locateRecursively(list,startingSearchDirname);
    list = locateInDirs(list,additionalPaths);

    String[] descriptors = new String[list.size()];
    return (String[]) list.toArray(descriptors);
  }
  
  /**
	 * Search for all meemkit descriptor files in a developer's
   * environment. The starting directory is assumed to contain directories
   * with the name *-meemit-* and the descriptor file is assumed to be located
   * at the top level of each of those.
	 * 
	 * @return An array of Strings of the descriptor filenames
	 */
	public String[] locateInProjects()
  {
    ArrayList<String> list = new ArrayList<String>();

    additionalPaths.add(startingSearchDirname);
    list = locateInDirs(list,additionalPaths);

    if ( list.size() == 0 )
    {
    	logger.log(Level.INFO, "locateInProjects() - no meemkit descriptors found");
      return null;
    }

    String[] descriptors = new String[list.size()];
    list.toArray(descriptors);

    return descriptors;    
  }
  
  /**
   * Search for all meemkit descriptor files in an ArrayList of directories.
   * Each directory is assumed to contain directories
   * with the name *-meemit-* and the descriptor file is assumed to be located
   * at the top level of each of those.
   * 
   * @param list      A list into which each meemkit descriptor's filename is placed
   * @param dirnames  An ArrayList of directories to search
   * @return          Returns the list of names
   */
  private ArrayList<String> locateInDirs(ArrayList<String> list, ArrayList dirnames)
  {
    if ( dirnames == null ) return list;

    for ( int i=0; i<dirnames.size(); i++ )
    {
      String dirname = (String) dirnames.get(i);
      File dir = new File(dirname);
      if ( dir.isDirectory() )
      {
        String[] possibleMeemkitDirs = dir.list();
        if ( possibleMeemkitDirs != null )
        {
          for ( int j=0; j<possibleMeemkitDirs.length; j++ )
          {
            String meemkitDir = possibleMeemkitDirs[j];
            if ( meemkitDir.indexOf("-meemkit-") >= 0 )
            {
              String descriptorName = dirname + File.separator + meemkitDir + File.separator + "meemkitDescriptor.xml";
              File file = new File(descriptorName);
              if ( file.exists() && file.isFile() )
              {
                list.add(descriptorName);
              }
            }
          }
        }
      }
    }
    
    return list;
  }

  /**
	 * Recursively search for meemkit descriptor files. A meemkit descriptor file
   * has the name 'meemkitdescriptor.xml'.
	 * 
	 * @param list A list into which each meemkit descriptor's filename is placed
	 * @param pathname The path to search
	 * @return An ArrayList containing all of the discovered meemkit descriptors
	 */
	private ArrayList<String> locateRecursively(ArrayList<String> list, String pathname)
  {
    File file = new File(pathname);
    if ( file.exists() == false ) return list;

    if ( file.isDirectory() == false )
    {
      if ( pathname.toLowerCase().endsWith("meemkitdescriptor.xml") )
      {
        list.add(pathname);
      }
      return list;
    }

    String[] files = file.list();
    if ( files == null ) return list;
    for ( int i=0; i<files.length; i++ )
    {
      String newPath = pathname + File.separator + files[i];
      locateRecursively(list,newPath);
    }
    return list;
  }
}
