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
import org.openmaji.meem.wedge.configuration.ConfigurationRejectedException;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.common.FloatPosition;
import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.diagnostic.Debug;
import org.openmaji.util.PositionHelper;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The SlowLoopbackLinearWedge is like a LoopbackLinearWedge except that it zeroes in
 * to a new Position from its current Position slowly over a period of time.
 * The new Position to zero-in on is received on the linearControlConduit and
 * configurable properties are used to set an initial value, an increment amount
 * and how quickly the new Position is reached.
 *
 * @author  Christos Kakris
 */

public class SlowLoopbackLinearWedge implements Wedge
{
  private static final Logger logger = Logger.getAnonymousLogger();
  private static final int DEFAULT_REFRESH_PERIOD = 2000;
  private static final FloatPosition DEFAULT_POSITION = new FloatPosition(0.0f,0.1f,-100,100);

  public Linear linearControlConduit = new LinearControlConduit();
  public Linear linearStateConduit;
  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
  public ThreadManager threadManagerConduit;
  public Debug debugConduit = new MyDebugConduit();
  public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

  public Position desiredPosition;
  public FloatPosition currentPosition = DEFAULT_POSITION;
  public int refreshPeriod = DEFAULT_REFRESH_PERIOD;

  public transient ConfigurationSpecification refreshPeriodSpecification = new ConfigurationSpecification("Refresh period in milliseconds",Integer.class,LifeCycleState.READY);
  public transient ConfigurationSpecification positionSpecification = new ConfigurationSpecification("Position specified as 'value increment min max'",String.class,LifeCycleState.READY);

  private int debugLevel;
  private volatile boolean stopRunning;
  private Runnable backgroundThread;

  /* -------------------- lifecycle methods --------------------------------- */

  public void commence()
  {
    startBackgroundThread();
    linearStateConduit.valueChanged(currentPosition);
  }

  private void startBackgroundThread()
  {
    if ( backgroundThread == null )
    {
      backgroundThread = new BackgroundThread();
      threadManagerConduit.queue(backgroundThread, System.currentTimeMillis() + refreshPeriod);
    }
  }

  public void conclude()
  {
    stopRunning = true;
  }

  /* ---------- configuration getters/setters ------------------------------- */

  public void setRefreshPeriod(Integer value)
  {
    refreshPeriod = value.intValue();
  }

  public int getRefreshPeriod()
  {
    return refreshPeriod;
  }

  public void setPosition(String positionString) throws ConfigurationRejectedException
  {
    currentPosition = PositionHelper.ParseFloatPosition(positionString);
    linearStateConduit.valueChanged(currentPosition);
  }

  public String getPosition()
  {
    return currentPosition.toParseableString();
  }

  /* ---------- LinearControlConduit ---------------------------------------- */

  private class LinearControlConduit implements Linear
  {
    public synchronized void valueChanged(Position position)
    {
      desiredPosition = position;
      startBackgroundThread();
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

  private class BackgroundThread extends Thread
  {
    public void run()
    {
      if ( stopRunning || desiredPosition == null )
      {
        if ( debugLevel > 0 )
        {
          logger.log(Level.INFO, "background thread terminating");
        }
        backgroundThread = null;
        return;
      }

      float delta = currentPosition.floatValue() - desiredPosition.floatValue();
      if ( Math.abs(delta) < 0.00001f )
      {
        if ( debugLevel > 0 )
        {
          logger.log(Level.INFO, "reached desired position - background thread terminating");
        }
        backgroundThread = null;
        return;
      }

      if ( delta < 0 )
      {
        currentPosition.increment();
      }
      else
      {
        currentPosition.decrement();
      }

      linearStateConduit.valueChanged(currentPosition);

      long delayMillis = System.currentTimeMillis() + refreshPeriod;
      threadManagerConduit.queue(this, delayMillis);
    }
  }
}
