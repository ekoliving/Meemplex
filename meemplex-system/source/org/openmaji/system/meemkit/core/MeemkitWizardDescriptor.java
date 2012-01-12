/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.io.Serializable;

/**
 * Describes the MeemkitWizard that can be installed into an IDE.
 *
 * @author Chris Kakris
 */
public class MeemkitWizardDescriptor implements Serializable
{
	private static final long serialVersionUID = 534540102928363464L;

  private final String text;
  private final String resourceClass;
  private final String imageFilename;
  private final String wizardClass;

  public MeemkitWizardDescriptor(String text, String wizardClass, String resourceClass, String imageFilename)
  {
    this.text = text;
    this.wizardClass = wizardClass;
    this.resourceClass = resourceClass;
    this.imageFilename = imageFilename;
  }

  public String getImageFilename()
  {
    return imageFilename;
  }

  public String getResourceClass()
  {
    return resourceClass;
  }

  public String getText()
  {
    return text;
  }

  public String getWizardClass()
  {
    return wizardClass;
  }

  public int hashCode()
  {
    return text.hashCode() ^ wizardClass.hashCode();
  }
}
