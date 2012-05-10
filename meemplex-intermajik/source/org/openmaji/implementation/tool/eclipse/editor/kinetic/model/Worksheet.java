/*
 * @(#)Worksheet.java
 * Created on 23/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;



/**
 * <code>Worksheet</code>.
 * <p>
 * @author Kin Wong
 */
public class Worksheet extends Diagram {
	
	private static final long serialVersionUID = 6424227717462161145L;

	static private Dimension DEFAULT_SIZE = new Dimension(100,100);
	
	public Worksheet(MeemClientProxy proxy) {
		super(proxy);
		setSize(DEFAULT_SIZE);
	}
}
