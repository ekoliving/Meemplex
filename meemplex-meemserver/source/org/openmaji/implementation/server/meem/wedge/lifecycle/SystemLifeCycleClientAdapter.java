/*
 * @(#)LifeCycleClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.lifecycle;

import java.lang.reflect.InvocationTargetException;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * Base class for a listener for a life cycle conduit for a system wedge.
 * <p>
 * The class provides standard application wedge monitoring for life cycle transitions and 
 * ordinarily you will only need to override the commence and conclude methods. Developers
 * needing finer grained control of the wedge as the parent meem's lifecycle state changes
 * should write a conduit implementation just based on LifeCycleClient.
 */
public class SystemLifeCycleClientAdapter 
	implements LifeCycleClient 
{
	private final Wedge parent;
	
	public ErrorHandler	errorHandlerConduit;
	
	public SystemLifeCycleClientAdapter(
		Wedge	parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Provide standard monitoring, calling commence or conclude as appropriate.
	 */
	public final void lifeCycleStateChanging(LifeCycleTransition transition)
	{
		if (transition.equals(LifeCycleTransition.DORMANT_LOADED)) 
		{
			try
			{
				parent.getClass().getMethod("commence", (Class[])null).invoke(parent, (Object[])null);
			}
			catch (NoSuchMethodException e)
			{
				// ignore
			}
			catch (InvocationTargetException e)
			{
				if (e.getCause() != null)
				{
					errorHandlerConduit.thrown(e.getCause());
				}
				else
				{
					errorHandlerConduit.thrown(e);
				}
			}
			catch (Exception e)
			{
				errorHandlerConduit.thrown(e);
			}
		}
		else if (transition.equals(LifeCycleTransition.LOADED_DORMANT)) 
		{
			try
			{
				parent.getClass().getMethod("conclude", (Class[])null).invoke(parent, (Object[])null);
			}
			catch (NoSuchMethodException e)
			{
				// ignore
			}
			catch (InvocationTargetException e)
			{
				if (e.getCause() != null)
				{
					errorHandlerConduit.thrown(e.getCause());
				}
				else
				{
					errorHandlerConduit.thrown(e);
				}
			}
			catch (Exception e)
			{
				errorHandlerConduit.thrown(e);
			}
		}
	}
	
	/**
	 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClient#lifeCycleStateChanged(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
	 */
	public void lifeCycleStateChanged(LifeCycleTransition transition)
    {
		// don't care
	}
}
