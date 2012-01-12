/*
 * @(#)EntryOfConnection.java
 * Created on 21/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.openmaji.implementation.tool.eclipse.editor.common.figures.MultiDecorationPolylineConnection;

/**
 * <code>EntryOfConnection</code>.
 * <p>
 * @author Kin Wong
 */
public class EntryOfConnection extends MultiDecorationPolylineConnection {
	public EntryOfConnection() {
		setLineWidth(2);
		setSourceDecoration(new EntryOfIndicatorDecoration());
	}
}
