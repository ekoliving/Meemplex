/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.deployment;

import java.util.*;

import org.jdom.Element;

public class DeviceMeemDescription
{
  private final String identifier;
  private final String type;
  private final String address;
  private final String description;

  /**
   * A set of hyperspace paths
   */
  private final Vector<String> hyperSpacePaths = new Vector<String>();

  public DeviceMeemDescription(Element rootElement)
  {
    this.identifier = rootElement.getAttributeValue("identifier");
    this.type = rootElement.getAttributeValue("type");
    
    Element element = rootElement.getChild("device-description");
    this.address = element.getAttributeValue("address");
    this.description = element.getAttributeValue("description");
    
    Element pathsElement = rootElement.getChild("paths");
    
    if (pathsElement != null) {
      List<?> elements = pathsElement.getChildren("path");
      for ( Iterator<?> iterator = elements.iterator(); iterator.hasNext(); )
      {
        Element pathElement = (Element) iterator.next();
        hyperSpacePaths.add(pathElement.getText());
      }
    }
  }

  public String getIdentifier()
  {
    return identifier;
  }

  public String getType()
  {
    return type;
  }

  public String getAddress()
  {
    return address;
  }

  public String getDescription()
  {
    return description;
  }
  
  public List<String> getHyperSpacePaths()
  {
    return hyperSpacePaths;
  }
  
  public String toString() {
	  StringBuffer sb = new StringBuffer();

	  sb.append('{');
	  sb.append(getClass());
	  sb.append(": ");
	  sb.append(getIdentifier());
	  sb.append(", ");
	  sb.append(getType());
	  sb.append(", ");
	  sb.append(getAddress());
	  sb.append('}');
	  
	  return sb.toString();
  }
}