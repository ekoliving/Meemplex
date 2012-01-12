/*
 * @(#)ThreadManagerWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.manager.thread;

import org.openmaji.implementation.server.nursery.statistics.StatisticsClient;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentProvider;



/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @deprecated use PoolingThreadManagerWedge
 * @author  Warren Bloomer, Andy Gelme
 * @version 1.0
 * @see org.openmaji.thread.ThreadManager
 */
class ThreadManagerWedge implements ThreadManager, Wedge
{	
    public MeemCore meemCore;

    public StatisticsClient	statsGatherer;
    public final ContentProvider statsGathererProvider = new ContentProvider() {
        public void sendContent(Object target, Filter filter)
        {
            ((StatisticsClient)target).statisticsChanged(new ThreadManagerStatistics(runner, atRunner));
        }
    };

	private TaskRunner runner = new TaskRunner();
	private TaskAtRunner atRunner = new TaskAtRunner();

    public void queue(
    	Runnable	runnable)
    {
        atRunner.queue(runnable, 0);
    }

	public void queue(Runnable	runnable, long absoluteTime)
	{
			atRunner.queue(runnable, absoluteTime);
	}
	
	public void cancel(Runnable runnable) {
	}
}
