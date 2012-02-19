/*
 * @(#)LayoutOptions.java
 * Created on 9/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.standard;

import java.io.Serializable;

/**
 * <code>LayoutOptions</code>.
 * <p>
 * @author Kin Wong
 */
public class LayoutOptions implements Serializable, Cloneable {
	private static final long serialVersionUID = 6424227717462161145L;

	static public Object DEPENDENCY = LayoutOptions.class.getName() + ".DEPENDENCY";
	static public Object DATA = LayoutOptions.class.getName() + ".DATA";
	
	private Object flow = DEPENDENCY;
	
	/**
	 * Constructs an instance of <code>LayoutOptions</code>.
	 * <p>
	 * @param flow
	 */
	public LayoutOptions(Object flow) {
		this.flow = flow;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			// Should never get to here.
			return null;
		}
	}

	public boolean isFlowDependency() {
		return flow.equals(DEPENDENCY);
	}

	public boolean isFlowData() {
		return flow.equals(DATA);
	}
}
