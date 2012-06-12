/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;



import java.util.logging.Level;
import java.util.logging.Logger;

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
      if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on linearListControlConduit");
      linearListStateConduit.valueChanged(posList);
    }
  }
  
  private static final Logger logger = Logger.getAnonymousLogger();
}
