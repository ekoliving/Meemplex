/*
 * @(#)LifeCycleManagement.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.lifecycle;

import org.openmaji.meem.Facet;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;


/**
 * <p>
 * Facet through which a meem is advised of changes in its life cycle management.
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleManagement extends Facet {
	
	/**
	 * Tell the Meem to change its parent LifeCycleManager. This should only be
	 * successful if the method is called by the meem's current LifeCycleManager.
	 * 
	 * @param lifeCycleManager The meems new LifeCycleManager.
	 */
	public void changeParentLifeCycleManager(LifeCycleManager lifeCycleManager);
}
