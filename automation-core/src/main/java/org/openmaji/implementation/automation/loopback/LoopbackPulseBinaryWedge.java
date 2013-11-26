/*
 * Copyright 2006 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.common.Binary;
import org.openmaji.diagnostic.Debug;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The LoopbackPulseBinaryWedge will produce a pulse on the binaryStateConduit
 * in response to an input on the binaryControlConduit. When it receives a 'true'
 * it will send a 'true' followed by a 'false' after a configurable period of time.
 * Similarly for a received 'false' input it will generate a 'false' followed by
 * a 'true'.
 */

public class LoopbackPulseBinaryWedge implements Wedge
{
  private static final Logger logger = Logger.getAnonymousLogger();
  public static final int DEFAULT_TRUE_PULSE_DURATION_MILLIS = 250;
  public static final int DEFAULT_FALSE_PULSE_DURATION_MILLIS = 250;

  public Binary binaryControlConduit = new BinaryControlConduit();
  public Binary binaryStateConduit = null;
  public ThreadManager threadManagerConduit;
  public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
  public Debug debugConduit = new MyDebugConduit();

  public int truePulseDuration = DEFAULT_TRUE_PULSE_DURATION_MILLIS;
  public int falsePulseDuration = DEFAULT_FALSE_PULSE_DURATION_MILLIS;

  private int debugLevel;

  public transient ConfigurationSpecification truePulseDurationSpecification = ConfigurationSpecification.create("True pulse duration in milliseconds",Integer.class,LifeCycleState.READY);
  public transient ConfigurationSpecification falsePulseDurationSpecification = ConfigurationSpecification.create("False pulse duration in milliseconds",Integer.class,LifeCycleState.READY);

  /* ---------- configuration getters/setters ------------------------------- */

  public void setTruePulseDuration(Integer value)
  {
    truePulseDuration = value.intValue();
  }

  public int getTruePulseDuration()
  {
    return truePulseDuration;
  }

  public void setFalsePulseDuration(Integer value)
  {
    falsePulseDuration = value.intValue();
  }

  public int getFalsePulseDuration()
  {
    return falsePulseDuration;
  }

  /* ---------- BinaryControlConduit ---------------------------------------- */

  class BinaryControlConduit implements Binary
  {
    public synchronized void valueChanged(boolean value)
    {
      binaryStateConduit.valueChanged(value);
      if ( debugLevel > 0 )
      {
        logger.log(Level.INFO, "Start of pulse, sent a '"+value+"' value");
      }

      if ( value )
      {
        Thread thread = new TruePulseThread();
        threadManagerConduit.queue(thread, System.currentTimeMillis() + truePulseDuration);
      }
      else
      {
        Thread thread = new FalsePulseThread();
        threadManagerConduit.queue(thread, System.currentTimeMillis() + falsePulseDuration);
      }
    }
  }

  /* ------------------------------------------------------------------------ */

  private class MyDebugConduit implements Debug
  {
    public void debugLevelChanged(int level)
    {
      debugLevel = level;
    }
  }

  /* ------------------------------------------------------------------------ */

  private class TruePulseThread extends Thread
  {
    public void run()
    {
      binaryStateConduit.valueChanged(false);
      if ( debugLevel > 0 )
      {
        logger.log(Level.INFO, "End of pulse, sent a 'false' value");
      }
    }
  }
  
  /* ------------------------------------------------------------------------ */

  private class FalsePulseThread extends Thread
  {
    public void run()
    {
      binaryStateConduit.valueChanged(true);
      if ( debugLevel > 0 )
      {
        logger.log(Level.INFO, "End of pulse, sent a 'true' value");
      }
    }
  }
}
