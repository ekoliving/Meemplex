/*
 * @(#)NamedMeemTransfer.java
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
 * <code>NamedMeemTransfer</code>.
 * <p>
 * @author Kin Wong
 */
public class NamedMeemTransfer extends SerializableTransfer {
	private static final String NAME_TRANSFER = NamedMeemTransfer.class + ".transfer";
	private static final String NAME_CLONE = NamedMeemTransfer.class + ".clone";
	
	public static final int TYPE_TRANSFER = registerType(NAME_TRANSFER);
	public static final int TYPE_CLONE = registerType(NAME_CLONE);
	
	private static NamedMeemTransfer instance = new NamedMeemTransfer(false);
	private static NamedMeemTransfer instanceClone = new NamedMeemTransfer(true);
	
	public static NamedMeemTransfer getInstance() {
		return instance;
	}
	public static NamedMeemTransfer getInstanceForClone() {
		return instanceClone;
		
	}
	
	private boolean cloning;
	
	/**
	 * Constructs an instance of <code>NamedMeemTransfer</code>.
	 * <p>
	 * @param cloning Whether the transfer is a cloning operation.
	 */
	protected NamedMeemTransfer(boolean cloning) {
		this.cloning = cloning;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		if(cloning) return new int[]{TYPE_CLONE};
		else				return new int[]{TYPE_TRANSFER};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		if(cloning) return new String[]{NAME_CLONE};
		else				return new String[]{NAME_TRANSFER};
	}
}
