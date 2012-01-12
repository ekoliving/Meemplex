/*
 * @(#)LifeCycleManagerCategory.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.category;

import org.openmaji.system.space.Category;

/**
 * <p>
 * This is a marker interface for LifeCycleManager categories.
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleManagerCategoryConduit extends Category {

	public void sendContent();

}
