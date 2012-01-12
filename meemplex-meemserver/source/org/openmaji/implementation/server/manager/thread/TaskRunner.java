/*
 * @(#)ObjectRunner.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.thread;

import java.util.ArrayList;

/**
 *
 */
public class TaskRunner
	extends Thread
{	
	private ArrayList	list = new ArrayList();
	
	long						taskCount = 0;
	long						startTime;
	long						busyTime;
	
	TaskRunner()
	{
		this.start();
	}
	
	void queue(
		Runnable	runnable)
	{
		synchronized (list)
		{
			taskCount++;
			
			list.add(runnable);
			
			list.notifyAll();
		}
	}

	public void run()
	{
		startTime = System.currentTimeMillis();
		
		for (;;)
		{
			Runnable	runnable = null;
		
			synchronized (list)
			{
				if (list.size() > 0)
				{
					runnable = (Runnable)list.get(0);
					list.remove(0);
				}
			}
			
			if (runnable != null)
			{
				long	taskStartTime = System.currentTimeMillis();
				
				try
				{
					runnable.run();
				}
				catch (Throwable e)
				{
					// ignore
				}
				
				busyTime = taskStartTime - System.currentTimeMillis();
			}
			else
			{
				synchronized (list)
				{
					try
					{
						list.wait();
					}
					catch (InterruptedException e)
					{
						// ignore 
					}
				}
			}
		}
	}
	
	/**
	 * return the number of tasks that have been queued.
	 */
	public long getTaskCount()
	{
		return taskCount;
	}
	
	public long getStartTime()
	{
		return startTime;
	}
	
	public long getBusyTime()
	{
		return busyTime;
	}
	
	public long getTasksWaiting()
	{
		long	waiting;
		
		synchronized (list)
		{
			waiting = list.size();
		}
		
		return waiting;
	}
}
