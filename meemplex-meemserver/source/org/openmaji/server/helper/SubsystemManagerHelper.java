/*
 * @(#)SubsystemManagerHelper.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.server.helper;

import org.openmaji.meem.Meem;

/**
 * @author Peter
 */
public class SubsystemManagerHelper
{
	public static SubsystemManagerHelper getInstance()
	{
		return instance;
	}

	public synchronized void setSubsystemManagerMeem(Meem subsystemManagerMeem)
	{
		if (this.subsystemManagerMeem != null)
		{
			throw new RuntimeException("SubsystemManager Meem has already been set.");
		}

		this.subsystemManagerMeem = subsystemManagerMeem;
		this.notifyAll();
	}

	public synchronized Meem getSubsystemManagerMeem()
	{
		while (subsystemManagerMeem == null)
		{
			try { this.wait(); } catch (InterruptedException e) {}
		}

		return subsystemManagerMeem;
	}

	private SubsystemManagerHelper()
	{
	}

	private static SubsystemManagerHelper instance = new SubsystemManagerHelper(); 

	private Meem subsystemManagerMeem; 
}
