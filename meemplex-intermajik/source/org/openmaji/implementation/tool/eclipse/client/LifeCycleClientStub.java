/*
 * @(#)LifeCycleClientStub.java
 * Created on 9/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;

/**
 * <code>LifeCycleClientStub</code>.
 * <p>
 * @author Kin Wong
 */
public class LifeCycleClientStub implements LifeCycleClient {

	/* (non-Javadoc)
	 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClient#lifeCycleStateChanging(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
	 */
	public void lifeCycleStateChanging(LifeCycleTransition transition) {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClient#lifeCycleStateChanged(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
	 */
	public void lifeCycleStateChanged(LifeCycleTransition transition) {
	}
}
