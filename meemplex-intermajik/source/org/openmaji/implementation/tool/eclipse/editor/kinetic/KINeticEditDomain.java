/*
 * @(#)KINeticEditDomain.java
 * Created on 7/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import org.eclipse.draw2d.Cursors;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IEditorPart;

/**
 * <code>KINeticEditDomain</code>.
 * <p>
 * @author Kin Wong
 */
public class KINeticEditDomain extends DefaultEditDomain {
	class FakeCommandStack extends CommandStack {
		public boolean canUndo() { return false; }
		public boolean canRedo() { return false; }
	}
	/**
	 * Constructs an instance of <code>KINeticEditDomain</code>.
	 * <p>
	 * @param editorPart
	 */
	public KINeticEditDomain(IEditorPart editorPart) {
		super(editorPart);
		setCommandStack(new FakeCommandStack());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.DefaultEditDomain#keyDown(org.eclipse.swt.events.KeyEvent, org.eclipse.gef.EditPartViewer)
	 */
	public void keyDown(KeyEvent keyEvent, EditPartViewer viewer) {
		if((keyEvent.keyCode & SWT.SHIFT) != 0) {
			ConnectionCreationTool connectionCreationTool = new ConnectionCreationTool();
			connectionCreationTool.setDefaultCursor(Cursors.CROSS);
			setActiveTool(connectionCreationTool);
			setDefaultTool(connectionCreationTool);
			
			//getEditorPart().getEditorSite().getActionBars().getStatusLineManager().setMessage("Click source facet to start dependency, click target facet to complete");
		} 
		//super.keyDown(keyEvent, viewer);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.DefaultEditDomain#keyUp(org.eclipse.swt.events.KeyEvent, org.eclipse.gef.EditPartViewer)
	 */
	public void keyUp(KeyEvent keyEvent, EditPartViewer viewer) {
		if((keyEvent.keyCode & SWT.SHIFT) != 0) {
			setDefaultTool(new SelectionTool());
			setActiveTool(getDefaultTool());
			//getEditorPart().getEditorSite().getActionBars().getStatusLineManager().setMessage("Hold down shift to create dependencies");
		} 
		//super.keyUp(keyEvent, viewer);
	}

	
}
