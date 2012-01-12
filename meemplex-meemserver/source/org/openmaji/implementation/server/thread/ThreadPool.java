/*
 * @(#)ThreadPool.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.thread;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.openmaji.implementation.server.Common;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * ThreadPool
 * 
 * A pool of Threads that can be reused.
 * 
 * @author Warren Bloomer
 * @version 1.0
 */

public class ThreadPool
    extends ThreadGroup
{

  int     counter       = 0;                 // thread counter for identification of threads
  long    maxWaitTime   = 5000;              // maximum length of time to wait for a Thread from the ThreadPool
  int     maxThreads    = 10;                // maximum number of threads in the pool
  Vector  freeThreads   = new Vector();      // a list of threads that are available
  HashSet usedThreads   = new HashSet();     // the set of threads that are currently allocated
  PoolMonitor monitor   = new PoolMonitor(); // for monitoring the used threads in the pool
  Object  syncObject    = new Object();      // for synchronization

  /**
   *
   * @param parent
   * @param name
   */
  ThreadPool(ThreadGroup parent, String name)
  {
    super(parent, name);
  }

  /**
   *
   * @param name
   */
  public ThreadPool(String name)
  {
    super(name);
  }

  /**
   *
   */
  public void startMonitor()
  	throws IllegalStateException
  {
    if (monitor.isRunning()) {
    	return;
      //throw new IllegalStateException("Monitor already running.");
    }
    Thread t = getThread(monitor);
    t.start();
  }

  /**
   *
   */
  public void stopMonitor()
  {
    monitor.stop();
  }

  /**
   * Override the ThreadGroup implementation
   *
   * @param thread
   * @param ex
   */
  public void uncaughtException(Thread thread, Throwable ex)
  {
    LogTools.warning(logger, "Uncaught exception for thread, " + thread, ex);
  }

  /**
   * Returns a thread for the Runnable object
   *
   * @param target the runnable object to run
   * @return a thread that is free to run the target
   */
  public  MonitoredThread getThread(Runnable target)
      throws RuntimeException
  {
    return getThread(target, null, -1);
  }

  /**
   *
   * @param target
   * @param reasonableTime
   * @return a free thread
   * @throws RuntimeException
   */
  public MonitoredThread getThread(Runnable target, long reasonableTime)
    throws RuntimeException
  {
    return getThread(target, null, reasonableTime);
  }

  /**
   *
   * @param target
   * @param name
   * @return a free thread
   * @throws RuntimeException
   */
  public MonitoredThread getThread(Runnable target, String name)
    throws RuntimeException
  {
    return getThread(target, name, -1);
  }

  /**
   * Returns a thread for the Runnable object
   *
   * @param target
   * @param name
   * @param reasonableTime
   * @return a free thread
   * @throws RuntimeException
   */
  public MonitoredThread getThread(Runnable target, String name, long reasonableTime)
    throws RuntimeException
  {
    MonitoredThread thread = null;

    if (target == null) {
      throw new RuntimeException("No Runnable target object supplied.");
    }

    long timeout  = maxWaitTime;
    long thisTime = System.currentTimeMillis();

    while (thread == null) {
      long lastTime = thisTime;
      synchronized (syncObject) {
        if (freeThreads.size() > 0) {
          thread = (MonitoredThread)freeThreads.remove(0);
          usedThreads.add(thread);
          syncObject.notifyAll();
        }
        else {
          // no free threads
          if (usedThreads.size() + freeThreads.size() < maxThreads) {
          	
            // create a new thread
            thread = new MonitoredThread(this, "Maji Pooled thread " + (counter++));
            usedThreads.add(thread);
            syncObject.notifyAll();
          }
          else {
          	
            // wait for free thread
            thisTime = System.currentTimeMillis();
            timeout = timeout - (thisTime - lastTime);
            if (timeout > 0) {
              try {
                syncObject.wait(timeout);
              }
              catch (InterruptedException ex) {
                LogTools.info(logger, "interrupted", ex);
                throw new RuntimeException(ex);
              }
            }
            else {
              throw new RuntimeException("Timeout waiting for Thread from the ThreadPool");
            }
          }
        }
      }
    }

    // set up thread
    thread.setTarget(target);

    if (name != null) {
      thread.setName(name);
    }
    thread.setReasonableTime(reasonableTime);

    return thread;
  }

  /**
   * Free the thread back into the pool
   *
   * @param thread
   */
  protected void free(MonitoredThread thread)
  {
	LogTools.trace(logger, Common.getLogLevelVerbose(), "Freeing thread: " + thread);
    synchronized (syncObject) {
      /*
      if (thread.isRunning()) {
      	LogTools.info(logger, "thread is still running. Interrupting.");
        thread.interrupt();
      }
      */
      usedThreads.remove(thread);
      if (usedThreads.size() + freeThreads.size() < maxThreads) {
        freeThreads.add(thread);
      }
      else {
      	// make sure thread is dead, perhaps thread.destroy()?
      	thread.interrupt();
      	//thread.destroy();
      }
      syncObject.notifyAll();
    }
  }
  
  /**
   * Remove the thread from this pool
   * 
   * @param thread
   */
  protected void remove(MonitoredThread thread)
  {
	synchronized (syncObject) {
		usedThreads.remove(thread);
		freeThreads.remove(thread);
	}
  }

  /**
   * set the maximum number of threads to store in the pool
   * @param max
   */
  public void setCapacity(int max)
  {
    synchronized (syncObject) {
      this.maxThreads = max;
      freeThreads.clear();
      syncObject.notifyAll();
    }
  }

  /**
   *
   * @return the maximum size of the pool
   */
  public int getCapacity()
  {
    return maxThreads;
  }

  /**
   *
   * @return the number of active threads
   */
  public int usedCount()
  {
    return usedThreads.size();
  }

  /**
   *
   * @return the number of available threads in the pool
   */
  public int freeCount()
  {
    return freeThreads.size();
  }


  /**
   * PoolMonitor
   * 
   * Monitors used "MonitoredThreads"
   */
  class PoolMonitor
      implements Runnable
  {
    long    checkTime  = 30000;
    boolean running    = false;

    public PoolMonitor()
    {
    }

    /**
     *
     */
    public void run()
    {
      for (running=true; running; ) {
        synchronized (syncObject) {
          checkThreads();
          LogTools.trace(logger, Common.getLogLevelVerbose(), "ThreadPool: " + usedCount() + " used, " + freeCount() + " free, " + getCapacity() + " capacity.");
          doWait();
        }
      }
    }

    /**
     *
     */
    public void stop()
    {
      synchronized (syncObject) {
        running = false;
        syncObject.notifyAll();
      }
    }

    public boolean isRunning()
    {
		synchronized (syncObject) {
	      return running;
		}
    }

    private void checkThreads()
    {
      // check for threads that are taking too long
      long currentTime = System.currentTimeMillis();
      Iterator iter = usedThreads.iterator();
      while (iter.hasNext()) {
        MonitoredThread thread = (MonitoredThread)iter.next();
        if (thread.isAlive()) {
          long reasonableTime = thread.getReasonableTime();
          if (reasonableTime > 0) {
            long startTime = thread.getStartTime();
            if ( (currentTime - startTime) > reasonableTime ) {
              LogTools.info(
                  logger,
                  "Thread, " + thread + ", is taking too long."
                );
                
              /** TODO[stormboy] put into "overdue" Set. Those that are too far overdue, interrupt or destroy. */
            }
          }
        }
      }
    }

    /**
     *
     */
    private void doWait()
    {
      synchronized (syncObject) {
        try {
          syncObject.wait(checkTime);
        }
        catch (InterruptedException ex) {
			running = false;
          LogTools.info(logger, "Interrupted", ex);
        }
      }
    }
  }
  

	/** logger */
	private static final Logger logger = LogFactory.getLogger();
}
