/*
 * @(#)LifeCycleManagerMisc.java
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
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleManagerMisc {
	
	public void buildMeem(MeemPath meemPath, MeemDefinition meemDefinition, int requestId);
	public void addLifeCycleReference(Meem meem);
	public void changeParentLifeCycleManager(Meem meem, LifeCycleManager targetLifeCycleManager);
}
