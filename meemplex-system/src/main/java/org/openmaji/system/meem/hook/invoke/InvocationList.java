/*
 * @(#)InvocationList.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.invoke;

import java.util.NoSuchElementException;

import org.openmaji.system.meem.hook.Hook;


/**
 * <p>
 * An InvocationList provides a sequence of Hooks to be processed.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.system.meem.hook.Hook
 */

public class InvocationList 
{
	private class InvocationIteratorImpl
		implements InvocationIterator
	{
		private Hook[]	hooks;
		private int			index = 0;
    
		 InvocationIteratorImpl(
			 Hook[]	hooks)
		 {
			 this.hooks = hooks;
		 }
    
		 public boolean hasNext()
		 {
			 return index != hooks.length;
		 }
    
		 public Hook nextHook()
		 {
			if (index == hooks.length)
			{
				throw new NoSuchElementException("no elements left in InvocationIterator.");
			}
			
			return hooks[index++];
		 }
		 
		 public Object next()
		 {
		 	return nextHook();
		 }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove()
        {
            throw new UnsupportedOperationException("remove not supported.");
        }
	}
	
	private Hook[]	hooks;
	
	public InvocationList(
		Hook[]	hooks)
	{
		this.hooks = hooks;
	}
	
	 /**
	  * Return a type safe iterator that contains the hooks making up the invocation list.
	  *
	  * @return iterator of hooks to be processed
	  */
	 public InvocationIterator invocationIterator()
	 {
	 	return new InvocationIteratorImpl(hooks);
	 }
}
