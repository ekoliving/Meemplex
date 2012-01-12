/*
 * @(#)MeemModelFactory.java
 * Created on 10/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.space.CategoryClient;



/**
 * <code>MeemModelFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemModelFactory {
	static public Meem create(MeemClientProxy proxy) {
		
		LifeCycleState	currentState = proxy.getLifeCycleState();
	
		int index = LifeCycleState.STATES.indexOf(currentState);
		int indexLoaded = LifeCycleState.STATES.indexOf(LifeCycleState.LOADED);
		if (index >= indexLoaded) {
			if(proxy.isA(CategoryClient.class)) {
				return new Category(proxy);
			}
		}
		return new Meem(proxy);
	}
}
