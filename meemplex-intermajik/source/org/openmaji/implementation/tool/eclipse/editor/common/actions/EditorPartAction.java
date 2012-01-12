/*
 * @(#)EditorPartAction.java
 * Created on 7/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.actions;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.ui.actions.UpdateAction;

/**
 * @author hudsonr
 */
public abstract class EditorPartAction
	extends WorkbenchPartAction
	implements Disposable, UpdateAction
{

/**
 * Used internally to avoid deprecation warnings in GEF subclasses.
 * @param part the part
 */
protected EditorPartAction(IWorkbenchPart part) {
	super(part);
}

/**
 * Used internally to avoid deprecation warnings in GEF subclasses.
 * @param part the part
 */
protected EditorPartAction(IWorkbenchPart part, int style) {
	super(part, style);
}

/**
 * Returns the editor associated with this action.
 * @return the Editor part
 */
protected IEditorPart getEditorPart() {
	return (IEditorPart)getWorkbenchPart();
}

/**
 * Sets the editor.
 * @param part the editorpart
 */
protected void setEditorPart(IEditorPart part) {
	setWorkbenchPart(part);
}

}