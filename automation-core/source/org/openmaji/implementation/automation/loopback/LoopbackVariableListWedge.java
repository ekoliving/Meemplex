/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.common.Value;
import org.openmaji.common.VariableList;
import org.openmaji.meem.Wedge;

/**
 * <p>
 * The LoopbackVariableWedge Wedge is a simple example of a pluggable Variable
 * thing.
 * </p>
 * <p>
 * LoopbackVariableWedge is a variableControlConduit target that listens
 * for Variable method invocations and immediately passes on those method
 * invocations as a variableStateConduit source.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not known
 * </p>
 * @author  Warren Bloomer
 */

public class LoopbackVariableListWedge implements Wedge
{
  public VariableList variableListControlConduit = new VariableListControlConduit();
  public VariableList variableListStateConduit = null;

  /* ---------- VariableControlConduit ----------------------------------------- */

  class VariableListControlConduit implements VariableList
  {
    public void valueChanged(Value[] valueList)
    {
      //if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"valueChanged() - invoked on VariableControlConduit");
    	LogTools.info(logger, "valueChanged() - invoked on variableListControlConduit");
      variableListStateConduit.valueChanged(valueList);
    }
  }
  
  private static final Logger logger = LogFactory.getLogger();
}
