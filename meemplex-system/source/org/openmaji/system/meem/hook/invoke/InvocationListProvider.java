/*
 * @(#)InvocationListProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.invoke;

/**
 * Interface profile for a provider of InvocationList objects.
 * 
 * @author Chris Kakris
 */
public interface InvocationListProvider
{
	/**
	 * Generate an invocation list.
	 * 
	 * @return an invocation list.
	 */
	public abstract InvocationList generate();
}