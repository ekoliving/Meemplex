/*
 * @(#)WedgeLibrary.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.library.wedgelibrary;

import org.openmaji.meem.Facet;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface WedgeLibrary extends Facet {

	/**
	 * Resets the list of known wedges. 
	 * Removes all subcategories. 
	 * Puts all re-discovered wedges into unfiled. 
	 */
	public void reset();

}
