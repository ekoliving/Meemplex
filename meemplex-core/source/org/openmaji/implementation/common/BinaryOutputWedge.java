/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common;

import org.openmaji.common.Binary;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinitionProvider;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

public class BinaryOutputWedge implements Wedge, WedgeDefinitionProvider
{
  public Binary binaryStateConduit = new BinaryStateConduit();
  public Binary binaryOutput;
  public final ContentProvider binaryClientProvider = new MyContentProvider();
  public boolean value = false;

  /* ---------- WedgeDefinitionProvider method(s) --------------------------- */

  public WedgeDefinition getWedgeDefinition()
  {
    return WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
  }  

  /* ---------- BinaryStateConduit ------------------------------------------ */

  private class BinaryStateConduit implements Binary
  {
    public void valueChanged(boolean newValue)
    {
      value = newValue;
      binaryOutput.valueChanged(value);
    }
  }

  /* ---------- ContentProvider --------------------------------------------- */

  private class MyContentProvider implements ContentProvider
  {
    public synchronized void sendContent(Object target, Filter filter)
    {
      Binary binary = (Binary) target;
      binary.valueChanged(value);
    }
  };

}
