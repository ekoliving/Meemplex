/*
 * @(#)WedgeLibraryClient.java
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

import org.openmaji.implementation.tool.eclipse.library.classlibrary.ClassDescriptor;
import org.openmaji.meem.Facet;



/**
 * <p>
 * WedgeLibrary outbound facet
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface WedgeLibraryClient extends Facet {
	
	public void wedgeAdded(ClassDescriptor wedgeClassDescriptor);
	
}
