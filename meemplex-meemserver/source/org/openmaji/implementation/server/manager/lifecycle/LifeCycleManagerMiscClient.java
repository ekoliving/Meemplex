/*
 * @(#)MeemBuilderClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleManagerMiscClient {
	
	/**
	 * This will be called when the meem has been registered and the LCM has a LC reference
	 * @param meem 
	 */
	public void lifeCycleReferenceAdded(Meem meem);
	
	/**
	 * This will be called after the meem has been created and set to a LOADED LC state
	 * @param meemPath
	 * @param meem
	 * @param requestId
	 */
	public void meemBuilt(MeemPath meemPath, Meem meem, int requestId);
	public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager targetLifeCycleManager);
	
	public void meemDeactivated(MeemPath meemPath);

}
