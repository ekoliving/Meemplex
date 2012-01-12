/*
 * @(#)LifeCycleManagementClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.lifecycle;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;


/**
 * <p>
 * The facet through which life cycle management changes are communicated.
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleManagementClient extends Facet {
	
	/**
	 * Notifies clients when the Meems parent LifeCycleManager has changed.
	 * 
	 * @param meem Meem whose LifeCycleManager as been changed
	 * @param lifeCycleManager New parent LifeCycleManager.
	 */
	public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager lifeCycleManager);
}
