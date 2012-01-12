/*
 * @(#)LifeCycleManagerCategoryClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.manager.lifecycle;

import org.openmaji.meem.Facet;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.CategoryClient;


/**
 * <p>
 * This is a marker interface for LifeCycleManager category clients.
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleManagerCategoryClient extends CategoryClient, ContentClient, Facet {

}
