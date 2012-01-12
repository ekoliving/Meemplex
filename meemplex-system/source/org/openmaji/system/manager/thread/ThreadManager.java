/*
 * @(#)ThreadManager.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.thread;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


/**
 * The ThreadManager is used to provide a mechanism for queuing runnable objects
 * to be executed by the meem server.
 */
public interface ThreadManager extends Facet {

	/**
	 * Property for specifying the number of active threads in Maji thread pool 
	 */
	public static final String PROPERTY_THREADMANAGER_ACTIVE_THREADS =
	  "org.openmaji.system.manager.thread.activeThreads";

    /**
     * Queue a runnable to be executed at the next available opportunity.
     * 
     * @param runnable the runnable to be performed.
     */
    public void queue(Runnable runnable);

	/**
	 * Queue a runnable to be executed as soon after the given time as possible.
	 *
	 * @param runnable the runnable to be performed.
	 * @param absoluteTime the earliest time it is to be executed.
	 */
	public void queue(Runnable runnable, long absoluteTime);

	/**
	 * Cancel the job
	 * @param runnable
	 */
	public void cancel(Runnable runnable);
	
    /**
     * Nested class for ServiceProvider.
     * 
     * @see org.openmaji.spi.MajiSPI
     */
    public class spi {
      public static ThreadManager create() {
        return((ThreadManager) MajiSPI.provider().create(ThreadManager.class));
      }

      public static String getIdentifier() {
        return("threadManager");
      };
    }
}