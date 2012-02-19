/*
 * @(#)MeemPathTransfer.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dnd;


/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class MeemPathTransfer extends SerializableTransfer {

	private static final String TYPE_NAME = "meempath_transfer";
	private static final int TYPE_ID = registerType(TYPE_NAME);

	private static MeemPathTransfer instance = new MeemPathTransfer();

	private MeemPathTransfer() {
	}

	public static MeemPathTransfer getInstance() {
		return instance;
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}
}
