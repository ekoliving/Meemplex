/*
 * @(#)CursorDescriptor.java
 * Created on 2/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import java.io.Serializable;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;

/**
 * <code>CursorDescriptor</code>.
 * <p>
 * @author Kin Wong
 */
public class CursorDescriptor implements Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	public static final int ARROW = SWT.CURSOR_ARROW;
	public static final int WAIT = SWT.CURSOR_WAIT;
	public static final int CROSS = SWT.CURSOR_CROSS;
	public static final int APPSTARTING = SWT.CURSOR_APPSTARTING;
	public static final int HELP = SWT.CURSOR_HELP;
	public static final int SIZEALL = SWT.CURSOR_SIZEALL;
	public static final int SIZENESW = SWT.CURSOR_SIZENESW;
	public static final int SIZENS = SWT.CURSOR_SIZENS;
	public static final int SIZENWSE = SWT.CURSOR_SIZENWSE;
	public static final int SIZEWE = SWT.CURSOR_SIZEWE;
	public static final int SIZEN = SWT.CURSOR_SIZEN;
	public static final int SIZES = SWT.CURSOR_SIZES;
	public static final int SIZEE = SWT.CURSOR_SIZEE;
	public static final int SIZEW = SWT.CURSOR_SIZEW;
	public static final int SIZENE = SWT.CURSOR_SIZENE;
	public static final int SIZESE = SWT.CURSOR_SIZESE;
	public static final int SIZESW = SWT.CURSOR_SIZESW;
	public static final int SIZENW = SWT.CURSOR_SIZENW;
	public static final int UPARROW = SWT.CURSOR_UPARROW;
	public static final int IBEAM = SWT.CURSOR_IBEAM;
	public static final int NO = SWT.CURSOR_NO;
	public static final int HAND = SWT.CURSOR_HAND;
	private Hashtable cursors;
	private int id = ARROW;
	/**
	 * Constructs an instance of <code>CursorDescriptor</code>.
	 * <p>
	 */
	public CursorDescriptor() {
	}
	public CursorDescriptor(int cursorId) {
		setCursor(cursorId);
	}
	public void setCursor(int cursorId) {
		this.id = cursorId;
	}
	public Cursor getCursor() {
		Cursor cursor = null;
		if(cursors != null) {
			cursor = (Cursor)cursors.get(new Integer(id));
		}
		else {
			cursors = new Hashtable();
		}
		if(cursor == null) {
			cursor = new Cursor(null, id);
			cursors.put(new Integer(id), cursor);
		}
		return cursor;
	}
}
