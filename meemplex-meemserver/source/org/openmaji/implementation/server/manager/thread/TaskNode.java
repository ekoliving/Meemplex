/*
 * @(#)TaskNode.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.thread;

import java.security.PrivilegedAction; 

import javax.security.auth.Subject;

/**
 * Node for containing the runnable and subject associated with a task.
 */
public class TaskNode
	implements Runnable
{
	private Subject		subject;
	private Runnable	task;
	
	private class PrivilegedRunnable
		implements PrivilegedAction
	{
		private Runnable	task;
		
		PrivilegedRunnable(
			Runnable	task)
		{
			this.task = task;
		}

        /**
         * @see java.security.PrivilegedAction#run()
         */
        public Object run()
        {
            task.run();
            
            return null;
        }
	}
	
	TaskNode(
		Subject	subject,
		Runnable	task)
	{
		if (subject == null)
		{
			throw new IllegalArgumentException("attempt to create task with null subject");
		}
		
		this.subject = subject;
		this.task = task;
	}
	
    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
    		Subject.doAsPrivileged(subject, new PrivilegedRunnable(task), null);
    }
}
