/*
 * @(#)LifeCycleRequest.java
 * Created on 15/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;


import org.eclipse.gef.Request;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * <code>LifeCycleRequest</code>.
 * <p>
 * @author Kin Wong
 */
public class LifeCycleRequest extends Request {
	static public final Object REQ_LIFE_CYCLE = LifeCycleRequest.class + ".request";
	
	private LifeCycleState state;

	public LifeCycleRequest(LifeCycleState state) {
		this.state = state;
		setType(REQ_LIFE_CYCLE);
	}
	
	/**
	 * Gets the Life Cycle State of this request.<p>
	 * @return The Life Cycle State of this request.
	 */
	public LifeCycleState getLifeCycleState() {
		return state;
	}
}
