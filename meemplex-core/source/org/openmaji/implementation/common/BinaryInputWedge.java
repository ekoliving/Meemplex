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
import org.openmaji.meem.definition.WedgeDefinitionUtility;

public class BinaryInputWedge implements Binary, Wedge, WedgeDefinitionProvider
{
  public Binary binaryControlConduit = null;

  /* ---------- Binary Facet method(s) -------------------------------------- */

  public synchronized void valueChanged(boolean value)
  {
    binaryControlConduit.valueChanged(value);
  }

  /* ---------- WedgeDefinitionProvider method(s) --------------------------- */

  public WedgeDefinition getWedgeDefinition()
  {
    WedgeDefinition wedgeDefinition = WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
    WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition,"binary","binaryInput");
    return wedgeDefinition;
  }  
}
