/*
 * @(#)MeemStatistics.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.statistics;

/**
 * Basic class for the statistics object.
 */
public class Statistics
{
	private long	timeStamp;
	
	public Statistics()
	{
		this(System.currentTimeMillis());
	}
	
	public Statistics(
		long	timeStamp)
	{
		this.timeStamp = timeStamp;
	}
	
	public long getTimeStamp()
	{
		return timeStamp;
	}
}
