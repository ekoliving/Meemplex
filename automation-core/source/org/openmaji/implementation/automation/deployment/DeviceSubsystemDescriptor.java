/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.deployment;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import org.openmaji.implementation.deployment.ConfigurationParameter;
import org.openmaji.implementation.deployment.Descriptor;

public class DeviceSubsystemDescriptor implements Descriptor
{
  private String identifier;
  private String type;
  private String defaultCategory;
  final private Collection<ConfigurationParameter> configurationParameters = new Vector<ConfigurationParameter>();
  final private Collection<DeviceMeemDescription> meemDescriptions = new Vector<DeviceMeemDescription>();

  public String getIdentifier()
  {
    return identifier;
  }

  public String getType()
  {
    return type;
  }

  public String getDefaultCategory()
  {
    return defaultCategory;
  }

  public Collection<ConfigurationParameter> getConfigurationParameters()
  {
    return configurationParameters;
  }

  public Collection<DeviceMeemDescription> getMeemDescriptions()
  {
    return meemDescriptions;
  }

  public void processElement(Element rootElement)
  {
    this.identifier = rootElement.getAttributeValue("identifier");
    this.type = rootElement.getAttributeValue("type");
    Element element = rootElement.getChild("hyperspace-default-category");
    this.defaultCategory = ( element == null ? null : element.getTextTrim() );

    List elements = rootElement.getChildren("property");
    for ( Iterator iterator = elements.iterator(); iterator.hasNext(); )
    {
      element = (Element) iterator.next();
      ConfigurationParameter cp = new ConfigurationParameter(element);
      configurationParameters.add(cp);
    }

    elements = rootElement.getChildren("meem");
    for ( Iterator iterator = elements.iterator(); iterator.hasNext(); )
    {
      Element meemElement = (Element) iterator.next();
      DeviceMeemDescription dmd = new DeviceMeemDescription(meemElement);
      meemDescriptions.add(dmd);
    }
  }

}
