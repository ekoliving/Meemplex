/*
 * @(#)Configuration.java
 * Created on 29/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.util.Hashtable;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;



/**
 * <code>Configuration</code> represents a configuration of interconnected 
 * meems.
 * @author Kin Wong
 */
public class Configuration extends Diagram {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * Constructs an instance of <code>Configuration</code>.
	 * <p>
	 * @param category The category associates with this configuration.
	 */
	public Configuration(MeemClientProxy category) {
		super(category);
	}
	
	protected Hashtable dependencyTable;	// table containing all dependencies

	/**
	 * Overridden to clear the internal dependency table.
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer#clear()
	 */
	public void clear() {
		super.clear();
		dependencyTable = null;
	}
	
	protected void addPropertyDescriptors(boolean readOnly) {
		// there are no property descriptors, so this does nothing
	}
}
