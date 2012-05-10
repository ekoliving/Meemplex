/*
 * @(#)Invocation.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.invoke;

import org.openmaji.meem.wedge.error.ErrorHandler;

/**
 * Profile interface for an invocation object.
 * 
 * @author Peter
 */
public interface Invocation
{
	/**
	 * Carry out the actual invocation on the given target.
	 * 
	 * @param target object the invocation is to be carried out on.
	 * @param errorHandler the handler errors are to be passed to.
	 */
	void invoke(Object target, ErrorHandler errorHandler);

	/**
	 * Return the facet identifier for the facet this invocation is target at.
	 *  
	 * @return the facet identifier for this invocation.
	 */
    String getFacetIdentifier();
    
    /**
     * Return a description of the underlying invocation.
     * 
     * @param detailed if true provide verbose style detail, if false provide something short.
     * @return the description of the invocation.
     */
	String getDescription(boolean detailed); 	
}
