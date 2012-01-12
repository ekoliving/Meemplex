/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.io.Serializable;

/**
 * Represents a Category entry in the Toolkit view including a path and an
 * optional image.
 *
 * @author Chris Kakris
 */

public class ToolkitCategoryEntry implements Serializable
{
	private static final long serialVersionUID = 534540102928363464L;

  private final String name;
  private final String path;
  private String iconName = null;

  /**
   * Create an instance with the specified name and path.
   * 
   * @param name  The name for this entry
   * @param path  The path that this entry
   */
  public ToolkitCategoryEntry(String name, String path)
  {
    this.name = name;
    this.path = path;
  }

  /**
   * Return the name of the entry
   * 
   * @return  The name of the entry
   */
  public String getName()
  {
    return name;
  }

  /**
   * Return the path represented by this entry.
   * 
   * @return  The path
   */
  public String getPath()
  {
    return path;
  }

  /**
   * Return the name of the icon to use for the toolkit category entry.
   * 
   * @return  The name of the icon
   */
  public String getIconName()
  {
    return iconName;
  }

  /**
   * Set the name of the icon to use for the toolkit entry.
   * 
   * @param iconName  The name of the icon to use for the toolkit entry
   */
  public void setIconName(String iconName)
  {
    this.iconName = iconName;
  }

}
