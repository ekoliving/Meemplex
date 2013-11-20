/*
 * @(#)InvocationIterator.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.invoke;

import java.util.Iterator;

import org.openmaji.system.meem.hook.Hook;


/**
 * Type safe iterator for invocation lists.
 */
public interface InvocationIterator
	extends Iterator
{
    public boolean hasNext();
    
    public Hook nextHook();
}
