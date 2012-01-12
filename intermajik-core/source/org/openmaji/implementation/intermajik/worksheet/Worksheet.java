/*
 * @(#)Worksheet.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.intermajik.worksheet;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface Worksheet extends Facet {
	
	public void lifeCycleManagerChanged(MeemPath lifeCycleManagerPath);

}
