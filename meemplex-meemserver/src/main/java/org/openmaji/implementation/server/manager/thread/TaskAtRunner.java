/*
 * @(#)ObjectRunner.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.thread;

import org.openmaji.system.manager.thread.Task;

public class TaskAtRunner extends Thread
{
	private long taskCount;

	TaskAtRunner()
	{}

	Task queue(Runnable runnable, long absoluteTime)
	{
		taskCount++;

		DelayedThread thread = new DelayedThread(runnable, absoluteTime);
		thread.start();
		return thread.getTask();
	}

	public long getTaskCount()
	{
		return taskCount;
	}
}
