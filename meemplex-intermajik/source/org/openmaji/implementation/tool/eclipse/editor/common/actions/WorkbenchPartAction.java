/*
 * @(#)WorkbenchPartAction.java
 * Created on 7/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.actions;
/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

/**
 * Base class for actions involving a WorkbenchPart. The workbench part is useful for
 * obtaining data needed by the action. For example, selection can be obtained using the
 * part's site. Anything can potentially be obtained using
 * {@link org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)}.
 */
public abstract class WorkbenchPartAction
	extends Action
	implements Disposable
{

private IWorkbenchPart workbenchPart;
private boolean lazyEnablement = true;

/**
 * Constructs a WorkbenchPartAction on the given part.
 * @param part the workbench part
 */
public WorkbenchPartAction(IWorkbenchPart part) {
	setWorkbenchPart(part);
	init();
}

public WorkbenchPartAction(IWorkbenchPart part, int style) {
	super("", style);
	setWorkbenchPart(part);
	init();
}

/**
 * Calculates and returns the enabled state of this action.  
 * @return <code>true</code> if the action is enabled
 */
protected abstract boolean calculateEnabled();

/**
 * Disposes the action when it is no longer needed.
 */
public void dispose() { }

/**
 * Executes the given {@link Command} using the command stack.  The stack is obtained by
 * calling {@link #getCommandStack()}, which uses <code>IAdapatable</code> to retrieve the
 * stack from the workbench part.
 * @param command the command to execute
 */
protected void execute(Command command) {
	if (command == null || !command.canExecute())
		return;
	getCommandStack().execute(command);
}

/**
 * Returns the editor's command stack. This is done by asking the workbench part for its
 * CommandStack via 
 * {@link org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)}.
 * @return the command stack
 */
protected CommandStack getCommandStack() {
	return (CommandStack)getWorkbenchPart().getAdapter(CommandStack.class);
}

/**
 * Returns the workbench part given in the constructor
 * @return the workbench part
 */
protected IWorkbenchPart getWorkbenchPart() {
	return workbenchPart;
}

/**
 * Initializes this action.
 */
protected void init() { }

/**
 * Returns <code>true</code> if the action is enabled. If the action determines enablemenu
 * lazily, then {@link #calculateEnabled()} is always called. Otherwise, the enablement
 * state set using {@link Action#setEnabled(boolean)} is returned.
 * @see #setLazyEnablementCalculation(boolean)
 * @return <code>true</code> if the action is enabled
 */
public boolean isEnabled() {
	if (lazyEnablement)
		return calculateEnabled();
	else
		return super.isEnabled();
}

/**
 * Refreshes the properties of this action.
 */
protected void refresh() {
	setEnabled(calculateEnabled());
}

/**
 * Sets lazy calculation of the isEnabled property.  If this value is set to
 * <code>true</code>, then the action will always use {@link #calculateEnabled()} whenever
 * {@link #isEnabled()} is called.
 * <P>Sometimes a value of <code>false</code> can be used to improve performance and
 * reduce the number of times {@link #calculateEnabled()} is called. However, the client
 * must then call the {@link #update()} method at the proper times to force the update of
 * enablement.
 * <P>
 * Sometimes a value of <code>true</code> can be used to improve performance. If an
 * <code>Action</code> only appears in a dynamic context menu, then there is no reason to
 * agressively update its enablement status because the user cannot see the Action. 
 * Instead, its enablement only needs to be determined when asked for, or <i>lazily</i>.
 * <P>
 * The default value for this setting is <code>true</code>.
 * @param value <code>true</code> if the enablement should be lazy
 */
public void setLazyEnablementCalculation(boolean value) {
	lazyEnablement = value;
}

/**
 * Sets the workbench part.
 * @param part the workbench part
 */
protected void setWorkbenchPart(IWorkbenchPart part) {
	workbenchPart = part;
}

public void update() {
	refresh();
}

}
