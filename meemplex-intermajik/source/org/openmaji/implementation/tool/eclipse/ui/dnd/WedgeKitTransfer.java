/*
 * @(#)WedgeKitTransfer.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dnd;


/**
 * <p>s
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class WedgeKitTransfer extends SerializableTransfer {
	private static final String TYPE_NAME = 
		WedgeKitTransfer.class.getName() + ".TRANSFER";
	private static final int TYPE_ID = registerType(TYPE_NAME);
	private static WedgeKitTransfer instance = new WedgeKitTransfer();
	
	/**
	 * Gets a static instance of WedgeKitTransfer.
	 */	
	public static WedgeKitTransfer getInstance() {
		return instance;
	}
	
	/**
	 * Constructs an instance of <code>WedgeKitTransfer</code>.
	 * <p>
	 */
	private WedgeKitTransfer() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}
}