/*
 * @(#)DependencyConnection.java
 * Created on 3/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.swt.SWT;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.MultiDecorationPolylineConnection;
import org.openmaji.implementation.tool.eclipse.editor.common.shapes.ArrowDecoration;


/**
 * <code>DependencyConnection</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyConnection extends MultiDecorationPolylineConnection {
	public DependencyConnection() {
		update(true, true, true);
	}
	
	public void update(boolean strong, boolean single, boolean sourceIsOutbound) {
		removeAllDecorations();
		if(strong) {
			setLineStyle(SWT.LINE_SOLID);
			setLineWidth(2);
		}
		else {
			setLineStyle(SWT.LINE_DOT);
			setLineWidth(0);
		}
		
		if(sourceIsOutbound) 
		setTargetDecoration(new ArrowDecoration());
		else 
		setSourceDecoration(new ArrowDecoration());
		setTargetDecoration(new DependencyIndicatorDecoration());
		if(!single) {
			setTargetDecoration(new DependencyManyIndicatorDecoration(50));
			setTargetDecoration(new DependencyManyIndicatorDecoration(60));
		}
	}
}
