/*
 * @(#)ThreadManagerStatistics.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.thread;

import org.openmaji.implementation.server.nursery.statistics.Statistics;


/**
 * 
 */
public class ThreadManagerStatistics
	extends Statistics
{
	long	runnerTasks;
	long	atRunnerTasks;
	
	ThreadManagerStatistics(
		TaskRunner		runner,
		TaskAtRunner	atRunner)
	{		
		runnerTasks = runner.getTaskCount();
		atRunnerTasks = atRunner.getTaskCount();
	}
	
	public long getTotalTasks()
	{
		return runnerTasks + atRunnerTasks;
	}
}
