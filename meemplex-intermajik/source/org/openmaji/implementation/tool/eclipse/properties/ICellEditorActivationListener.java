/*
 * @(#)ICellEditorActivationListener.java
 * Created on 28/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import org.eclipse.jface.viewers.CellEditor;

/**
 * <code>ICellEditorActivationListener</code>.
 * <p>
 * @author Kin Wong
 */
public interface ICellEditorActivationListener {
	/**
	 * Notifies that the cell editor has been activated
	 *
	 * @param cellEditor the cell editor which has been activated
	 */
	public void cellEditorActivated(CellEditor cellEditor);
	/**
	 * Notifies that the cell editor has been deactivated
	 *
	 * @param cellEditor the cell editor which has been deactivated
	 */
	public void cellEditorDeactivated(CellEditor cellEditor);
}
