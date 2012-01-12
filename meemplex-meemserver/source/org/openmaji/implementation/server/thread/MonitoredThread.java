/*
 * @(#)MonitoredThread.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.thread;


import org.openmaji.implementation.server.Common;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * MonitoredThread.
 * 
 * Represents a Thread that is monitored.
 * 
 * @author Warren Bloomer
 * @version 1.0
 */

public class MonitoredThread
    extends Thread
{
  long    startTime      = -1;
  long    finishTime     = -1;
  long    reasonableTime = 10000;   // reasonable time for this thread to complete
  boolean running     = false;

  Runnable   target   = null;
  ThreadPool pool     = null;
  
  boolean firstTime = true;

  /**
   * Constructor
   * @param pool the thread pool that this thread belongs to
   * @param name the name of this thread
   */
  public MonitoredThread(ThreadPool pool, String name)
  {
	super(pool, name);
    this.pool = pool;
  }

  /**
   *
   * @param target the runnable object to run
   */
  protected void setTarget(Runnable target)
  {
    this.target = target;
    startTime   = -1;
    finishTime  = -1;
  }

	/**
	 * Run the runnable target
	 * 
	 * Note the thread has to be kept alive, otherwise the thread loses its ThreadGroup and becomes unusable.
	 */
	public synchronized void run()
	{
		try {
			while (true) {
				running = true;
				startTime = System.currentTimeMillis();
				try {
					// start the runnable object
					target.run();
				}
				finally {
					finishTime = System.currentTimeMillis();
					running = false;
					// release the thread from the pool
					pool.free(this);
				}
				
				// keep thread alive but available
				synchronized (this) {
					try {
						wait();
					}
					catch (InterruptedException ex) {
						LogTools.trace(logger, Common.getLogLevel(), "Interrupted monitored thread");
						break;
					}
				}
			}
		}
		finally {
			// make sure thread is removed from the pool
			pool.remove(this);
		}
	}

	/**
	 * 
	 */
	public synchronized void start() {
		if (firstTime) {
			super.start();
			firstTime = false;
		}
		else {
			if (isRunning()) {
				throw new IllegalThreadStateException("Thread is already running");
			}
			notify();     // wake up this thread
		}
	}
	
  /**
   * Sets the time that is reasonable for this thread to be used
   *
   * @param milliseconds
   */
  public void setReasonableTime(long milliseconds)
  {
    this.reasonableTime = milliseconds;
  }

  /**
   * Time reasonable for this thread to complete it's task.
   *
   * @return time in milliseconds
   */
  public long getReasonableTime()
  {
    return reasonableTime;
  }

  /**
   *
   * @return whether the thread is running or not
   */
  public boolean isRunning()
  {
    return running;
  }

  /**
   *
   * @return the time the thread was started
   */
  public long getStartTime()
  {
    return startTime;
  }

  /**
   *
   * @return the time the thread had completed running the task
   */
  public long getFinishedTime()
  {
    return finishTime;
  }
  
  /**  logger */
  private static final Logger logger = LogFactory.getLogger();
}