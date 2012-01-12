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

import org.openmaji.implementation.common.DebugFlag;

import org.openmaji.common.LinearList;
import org.openmaji.common.Position;
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

public class LoopbackLinearListWedge implements Wedge
{
  public LinearList linearListControlConduit = new LinearListControlConduit();
  public LinearList linearListStateConduit = null;

  /* ---------- VariableControlConduit ----------------------------------------- */

  class LinearListControlConduit implements LinearList
  {
    public void valueChanged(Position[] posList)
    {
      if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"valueChanged() - invoked on linearListControlConduit");
      linearListStateConduit.valueChanged(posList);
    }
  }
  
  private static final Logger logger = LogFactory.getLogger();
}
