/*
 * @(#)ClientSynchronizer.java
 * Created on 27/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

/**
 * <code>ClientSynchronizer</code> defines the contracts for a client proxy to 
 * dispatch outbound facet method invocations from Maji on a specific thread. 
 * For outbound facet implementation that may trigger User Interface changes, 
 * a <code>ClientSynchronizer</code> must be implemented to normalise execution 
 * to the main UI thread.<p>
 * @author Kin Wong
 * @see java.lang.Runnable
 * @see org.openmaji.implementation.tool.eclipse.client.MeemClientProxy
 */
public interface ClientSynchronizer {
	/**
	 * Implemented to allows execution of a <code>Runnable</code> to be done
	 * on thread other than the worker thread from Maji, possibly the main 
	 * UI thread.
	 * <p>
	 * @param runnable The <code>Runnable</code> to be executed on another thread.
	 */
	void execute(Runnable runnable);
}
