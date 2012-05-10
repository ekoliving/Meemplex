/*
 * @(#)MeemPlex.java
 * Created on 23/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;


/**
 * <code>MeemPlex</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemPlex extends Meem {
	
	private static final long serialVersionUID = 6424227717462161145L;

	static private Dimension INITIAL_SIZE = new Dimension(250, 200);
	private Worksheet worksheet;
	
	/**
	 * Constructs an instance of <code>MeemPlex</code>.
	 * <p>
	 * @param proxy
	 */
	public MeemPlex(MeemClientProxy proxy) {
		super(proxy);
		setViewMode(ViewModeConstants.VIEW_MODE_DETAILED);
		setSize(INITIAL_SIZE);
	}
	
	/**
	 * Gets the worksheet associates with this meemplex.
	 * @return Worksheet The worksheet associates with this meemplex.
	 */
	public Worksheet getWorksheet() {
		return worksheet;
	}
}
