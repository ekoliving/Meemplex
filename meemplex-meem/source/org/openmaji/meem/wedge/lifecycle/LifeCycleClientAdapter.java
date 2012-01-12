/*
 * @(#)LifeCycleClientAdapter.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.wedge.lifecycle;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.error.ErrorHandler;


/**
 * Base class for a listener for a life cycle conduit.
 * <p>
 * The class provides standard application wedge monitoring for life cycle transitions and 
 * ordinarily you will only need to implement commence and conclude methods in your wedge
 * which the conduit adapter will invoke via reflection. Developers
 * needing finer grained control of the wedge as the parent meem's lifecycle state changes
 * should write a conduit implementation just based on {@link LifeCycleClient}.
 * </p>
 * <p>
 * The adapter is used by attaching it to the lifeCycleClientConduit as folows:
 * <pre>
 * public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
 * </pre>
 * </p>
 * The class looks for three methods that can be defined on a wedge:
 * <ul>
 * <li>validate - called prior to commence to see if the wedge is able to go ready - if the wedge is not
 * in a valid state the method should throw a {@link WedgeValidationException} passing the reason
 * as an argument.
 * <li>commence - called as the wedge goes to pending.
 * <li>conclude - called as the wedge is taken back to a loaded state.
 * </ul>
 */
public class LifeCycleClientAdapter 
	implements LifeCycleClient 
{
	private Wedge 	parent;
	
	public MeemContext meemContext;
	
	/**
	 * The conduit through which we pass on errors.
	 */
	public ErrorHandler   errorHandlerConduit;
	
	/**
	 * The conduit through which this Wedge will signal whether or not is able to go READY
	 */
	public Vote lifeCycleControlConduit;
	
	public LifeCycleClientAdapter(
		Wedge	parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Provide standard monitoring, calling commence or conclude as appropriate.
	 */
	public final void lifeCycleStateChanging(LifeCycleTransition transition)
	{
		if (transition.equals(LifeCycleTransition.LOADED_PENDING)) 
		{
			try
			{
				parent.getClass().getMethod("validate", (Class[])null).invoke(parent, (Object[])null);
				
				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), true);
			}
			catch (NoSuchMethodException e)
			{
				// ignore - we'll assume the wedge will deal with this itself.
			}
			catch (InvocationTargetException e)
			{
				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
				if (e.getCause() != null)
				{
					if (!(e.getCause() instanceof WedgeValidationException))
					{
						errorHandlerConduit.thrown(e.getCause());
					}
					else
					{
						logger.log(Level.WARNING, e.getCause().getMessage());
					}
				}
				else
				{
					errorHandlerConduit.thrown(e);
				}
				return;
			}
			catch (Exception e)
			{
				errorHandlerConduit.thrown(e.getCause());
				
				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
				return;
			}
			
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
		else if (transition.equals(LifeCycleTransition.PENDING_LOADED)) 
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
	public void lifeCycleStateChanged(LifeCycleTransition transition) {
		// don't care
	}

	private static final Logger logger = Logger.getAnonymousLogger();
}
