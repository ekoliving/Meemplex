/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.diagnostic;

import java.util.logging.Logger;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;

/**
 * This diagnostic Wedge logs all of the LifeCycle transitions that it
 * experiences, and nothing else. You can add this wedge to one of your
 * Meems to see what LifeCycle changes it experiences to help diagnose
 * problems.
 * 
 * @author Chris Kakris
 */

public class LifeCycleTransitionLoggerWedge implements Wedge
{
  private static Logger logger = Logger.getAnonymousLogger();

  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientHandler();

  /* ------------------------------------------------------------------------ */

  private class LifeCycleClientHandler implements LifeCycleClient
  {
    public void lifeCycleStateChanging(LifeCycleTransition transition)
    {
      StringBuffer buffer = new StringBuffer();
      buffer.append("CHANGING: ");
      buffer.append(transition.getPreviousState().getCurrentState());
      buffer.append(" -> ");
      buffer.append(transition.getCurrentState().getCurrentState());
      logger.info(buffer.toString());
    }

    public void lifeCycleStateChanged(LifeCycleTransition transition)
    {
      StringBuffer buffer = new StringBuffer();
      buffer.append("CHANGED:  ");
      buffer.append(transition.getPreviousState().getCurrentState());
      buffer.append(" -> ");
      buffer.append(transition.getCurrentState().getCurrentState());
      logger.info(buffer.toString());
    }
  }
}
